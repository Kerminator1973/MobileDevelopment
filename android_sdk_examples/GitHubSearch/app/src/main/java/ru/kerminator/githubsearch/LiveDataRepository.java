package ru.kerminator.githubsearch;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LiveDataRepository {

    private static LiveDataRepository instance;

    private LiveDataRepository() {}

    public static LiveDataRepository getInstance() {

        if (instance == null) {

            instance = new LiveDataRepository();
            Log.i("Info", "Creating the LiveDataRepository");
        }

        return instance;
    }

    // Объект используется для хранения данных с жизненным циклом, соответствующим приложению
    private static MutableLiveData<String> liveData = new MutableLiveData<>();

    // Метод позволяет получить объект LiveData и использовать его в Observer-е
    LiveData<String> getData() {
        return liveData;
    }

    // Метод позволяет сохранить изменения в LiveData
    void setData(String value) {
        liveData.setValue(value);
    }
}
