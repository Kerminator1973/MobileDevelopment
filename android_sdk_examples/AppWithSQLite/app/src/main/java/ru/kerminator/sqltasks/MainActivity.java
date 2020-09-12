package ru.kerminator.sqltasks;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import ru.kerminator.sqltasks.database.AppDatabase;
import ru.kerminator.sqltasks.database.TaskEntry;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity
        implements TasksAdapter.ListItemClickListener {

    // Определяем константу для логирования с именем класса. Эта константа
    // будет использоваться в Logcat
    private static final String TAG = MainActivity.class.getSimpleName();

    // Определяем ссылку на RecyclerView и адаптер для создания
    // ViewHolder и связывания его с данными (binding)
    private TasksAdapter mAdapter;
    private RecyclerView mTasksList;

    // Создаём singleton для работы с локальной базой данных SQLite
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Создаём генератор случайных чисел
        final RandomString generator = new RandomString();

        // Добавляем обработчик кнопки "Add"
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Формируем данные для сохранения в базе данных
                Date date = new Date();
                String ubiqueString = generator.nextString();

                // Выводим сообщение о попытке добавлении новой записи в базу данных
                StringBuilder sb = new StringBuilder();
                sb.append("A new item '");
                sb.append(ubiqueString);
                sb.append("' is adding to the database");

                Snackbar.make(view, sb.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Асинхронно добавляем записи в базу данных
                final TaskEntry taskEntry = new TaskEntry(ubiqueString, date);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Выполняем операцию INSERT в базу данных
                        mDb.taskDao().insertTask(taskEntry);
                    }
                });
            }
        });

        ///////////////////////////////////////////////////////////////////////
        // Действия связанные с RecyclerView

        // Получаем ссылку на RecyclerView текущей Activity
        mTasksList = findViewById(R.id.rv_tasks);

        // Добавляем компонент управления разметкой внутри RecyclerView
        // и для оптимизации, устанавливает фиксированную высоту элементов
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTasksList.setLayoutManager(layoutManager);
        mTasksList.setHasFixedSize(true);

        // Связываем адаптер доступа к данным с RecyclerView
        mAdapter = new TasksAdapter(this, this);
        mTasksList.setAdapter(mAdapter);

        DividerItemDecoration decoration =
                new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mTasksList.addItemDecoration(decoration);

        ///////////////////////////////////////////////////////////////////////
        // Создаём singleton для работы с базой данных и считываем все
        // ранее добавленные задачи

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();

        ///////////////////////////////////////////////////////////////////////
        // Обработка жеста "swipe" для удаления элемента списка

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                Log.v("Gestures", "the Swipe gesture is detected");

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Получаем позицию удаляемого элемента в RecyclerView
                        // и удаляем элемент из адаптера и из базы данных
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks = mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }

        }).attachToRecyclerView(mTasksList);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        // Получаем выбранный элемент и обрабатываем его
        if (clickedItemIndex >= 0 && clickedItemIndex < mAdapter.getItemCount()) {
            Log.v(TAG, "onListItemClick()");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Метод, который связывает объект ViewModel, осуществляющий кэширование запросов
    // из базы данных с Observer-ом, отслеживающим изменения в базе данных через
    // ViewModelProvider
    private void setupViewModel()
    {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }
}
