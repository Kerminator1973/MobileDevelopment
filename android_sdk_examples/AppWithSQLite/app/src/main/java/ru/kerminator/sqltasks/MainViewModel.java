package ru.kerminator.sqltasks;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.kerminator.sqltasks.database.AppDatabase;
import ru.kerminator.sqltasks.database.TaskEntry;


// Данный класс является ViewModelProvider и предоставляет доступ
// к данным из базы данных с использованием LiveData<>
public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllTasks();
    }

    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }
}
