# Цель репозитария

Приложение иллюстрирует три типовые задачи:

1. Отображать список и управлять им (RecycleView)
2. Удалять элемент из списка используя жест (gesture) "смахивание" (swipe)
3. Управлять данными в базе SQLite (локальная база данных Android)

# Создание RecycleView

В типовой схеме, **Activity** владеет контейнером с данными, которые следует отображать в списке. Часто используется ArrayList<> специализируемый некоторым типом:

```java
ArrayList<TaskDescription> mTaskDescriptions = new ArrayList<>();
```

Тип, используемый в специализации - обычный Java-класс (но это может быть тип используемый в Room, или REST), например:

```java
public class TaskDescription {

    String name;
    String priority;

    TaskDescription(String name, String priority) {
        this.name = name;
        this.priority = priority;
    }
}
```

В разметке (Layout) связанной с Activity определяется **RecycleView**. Например так:

```xml
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/rv_tasks"/>
```

Каждым элементом RecylerView является шаблон разметки, который также определяется в xml-файле, чаще всего размещаемом в папке "res/layout". В этом шаблоне могут использоваться разные органы управления, например, TextView. Ниже приведён пример такого шаблона:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="6dp" >

    <TextView
        android:id="@+id/tv_item_task_name"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:fontFamily="monospace" android:textColor="#000000" android:textSize="24sp" />

    <TextView
        android:id="@+id/tv_item_task_priority"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:fontFamily="monospace" android:textSize="20sp" />

</LinearLayout>
```

Задача программиста состоит в том, чтобы связать данные из ArrayList с полями шаблона элемента списка. Для решения этой задачи используется адаптер - класс расширяющий **RecyclerView.Adapter<>**. В этом классе может храниться копия контейнера данных (RecyclerView работает в потоке пользовательского интерфейса, что ограничивает возможности выполнения синхронных операций), обработчики событий, callback-функции и т.д. В классе, производном от RecyclerView.Adapter<> должны быть определены следующие методы: **onCreateViewHolder**(), **onBindViewHolder**(), **getItemCount**().

Метод getItemCount() возвращает количество элементов в списке (RecyclerView).

Первые два метода отвечают за связывание данных из RecyclerView с т.н. ViewHolder-ом, внутренним классом адаптера (может быть и внешним классом), который связывает элементы контейнера с полями шаблона элемента списка.

Реализация onBindViewHolder() чаще всего примитивна - в ней вызывается соответствующий метод ViewHolder-а:

```java
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {
...
    private ArrayList<TaskDescription> mTaskDescriptions;
...
	@Override
	public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
		holder.bind(position);
	}

	class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	...
		void bind(int index) {

			TaskDescription task = mTaskDescriptions.get(index);

			listItemNameView.setText(task.name);
			listItemPriorityView.setText(task.priority);
		}
	...
	}
};
```

Задача метода onCreateViewHolder() - непосредственно сконструировать новый элемент списка, с использованием шаблона разметки. Поскольку мы управляем созданием каждого элемента из шаблона, *можно предположить, что элементы в RecyclerView могут быть разных типов*. Пример:

```java
@NonNull
@Override
public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

	Context context = parent.getContext();

	int layoutIdForListItem = R.layout.task_description;
	LayoutInflater inflater = LayoutInflater.from(context);
	boolean shouldAttachToParentImmediately = false;

	View view = inflater.inflate(layoutIdForListItem, parent,
			shouldAttachToParentImmediately);
	TasksViewHolder viewHolder = new TasksViewHolder(view);

	return viewHolder;
}
```

# Обработка жеста "смахивание" (swipe)

Helper выполняет всю необходимую работы по настройке подсистемы обработки жестов. Вызов helper-а должен осуществляться после инициализации адаптера доступа к данным. Типовая реализация выглядит так:

```java
public class MainActivity extends AppCompatActivity
        implements TasksAdapter.ListItemClickListener {
		
    private TasksAdapter mAdapter;
    private RecyclerView mTasksList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	...
	mTasksList = findViewById(R.id.rv_tasks);
	...
    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) 
		{
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.v("Gestures", "the Swipe gesture is detected");

                // Удаляем элемент из контейнера и перезагружаем список. Это
                // нужно делать, чтобы убрать пустые позиции в RecyclerView,
                // которые появляются при применении жеста swipe "по умолчанию"
                int position = viewHolder.getAdapterPosition();
                mAdapter.deleteItem(position);
            }
        }).attachToRecyclerView(mTasksList);
    }
```

«По умолчанию», при применении жеста «смахивание» RecyclerView убирает элемент с экрана, но оставляет пустое место (placeholder). Чтобы убрать это пустое место нужно явным образом изменить контейнер данных в адаптере и уведомить об этом RecyclerView.

# Room - Object-Relational Mapping в Android

**Room** является критичным компонентом для приложений, которые предназначены для работы в **offline-режиме**. При первом запуске система считывает данные из облака и сохраняет в локальную базу данных, затем с этом базой осуществляется некоторая работа в режиме offline, а затем, при появлении доступа в сеть, данные синхронизуются.

Библиотеки добавляются с секцию dependencies, файла «build.gradle». Для добавления Room следует использовать следующие директивы:

```
implementation "android.arch.persistence.room:runtime:1.1.1"
annotationProcessor "android.arch.persistence.room:compiler:1.1.1"
```

Я добавляю зависимости через меню «File -> **Project Structure**». В соответствующей форме наживаю кнопку «+» и выбираю «Library Dependency». В появившемся окне поиска, выполняю поиск по маске, например: «*.persistence.room». Для проверки смотрю на записи добавленные в файл «build.gradle» (Module: app). Такой подход позволяет установить наиболее актуальную версию, поскольку можно посмотреть последнюю доступную версию компонента в репозитарии.

Для использования ROOM следуют разработать файлы четырёх типов: классы описания таблицы (Entries), класс доступа к сущностям базы (Dao), конвертеры типов данных (DataConverters) и класс "база данных приложения" (AppDatabase). Последовательность разработки обычно такая: Entry-классы, DataConverter, Dao, AppDatabase.

Пример Entry-класса:

```java
package ru.kerminator.sqltasks.database;

import ...

// Описание таблицы базы данных
@Entity(tableName = "task")
public class TaskEntry {

    // Поле "id" - основной ключ, auto increment
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Поле для хранения сгенерированных случайным образом данных
    private String description;

    // Поле для хранения временного штампа, по которому может
    // осуществляться контроль хранения данных в базе
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Конструктор используется для создания новой записи. По каким-то
    // причинам (TODO: нужно разобраться!), для Room нужно применять к
    // этому конструктору макрос @Ignore
    @Ignore
    public TaskEntry(String description, Date updatedAt) {
        this.description = description;
        this.updatedAt = updatedAt;
    }

    // Конструктор для создания объекта, который будет считываться из записи базы данных
    public TaskEntry(int id, String description, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    // Далее следуют setter-ы и getter-ы

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

Пример Dao-класса:

```java
package ru.kerminator.sqltasks.database;

import ...

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY updated_at")
    List<TaskEntry> loadAllTasks();

    @Insert
    void insertTask(TaskEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);

    @Delete
    void deleteTask(TaskEntry taskEntry);
}
```

Пример AppDatabase:

```java
package ru.kerminator.sqltasks.database;

import ...

@Database(entities = {TaskEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract TaskDao taskDao();
}
```

# Выполнение запросов к базе данных в отдельном потоке: Executors vs LiveData<>

Операции взаимодействия с базой данных относятся к синхронным операциям ввода/вывода, что не позволяет использовать их (есть исключения, но здесь я их не буду рассматривать) в потоке пользовательского интерфейса. Таким образом, необходим механизм, который будет выполнять запросы к базе данных в отдельном рабочем потоке, а затем обрабатывать результат работы в потоке пользовательского интерфейса.

Если операции с базой данных осуществляются систематически, наиболее разумным является создание рабочего потока один раз и его повторного использования тогда, когда нам это нужно. Именно эту задачу и решают исполнители (Executors).

Вместе с тем, если мы начинаем применять LiveData<> и Observer, нам уже не нужны Executor-ы (точнее - нужны, но в меньшей степени, преимущественно, для выполнения опреация INSERT/UPDATE).

Для того, чтобы не перегружать данные из базы при повороте экрана, следует добавить **ViewModel** в проект.
