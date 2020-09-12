package ru.kerminator.githubsearch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Объект реализующий GitHub REST API
    GitHubService mClient;

    // Ссылки на органы управления
    private EditText mSearchBox;
    private TextView mGitHubSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Обеспечиваем доступ к органам управления Activity
        mSearchBox = findViewById(R.id.et_search_box);
        mGitHubSearchResult = findViewById(R.id.tv_github_search_results_json);

        // Создаём экземпляр класса, реализующий GitHub REST API
        mClient =  ServiceGenerator.getInstance().create(GitHubService.class);

        // С помощью Singleton-а LiveDataRepository получаем экземпляр класса
        // LiveData<String> в котором данные могут храниться в период между
        // удалением и повторным созданием Activity
        LiveData<String> liveData = LiveDataRepository.getInstance().getData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                // Получаем данные посредством LiveData и помещаем в строку редактирования
                mGitHubSearchResult.setText(value);
            }
        });

        // Регистрируем callback-функцию, обрабатывающую любые изменения
        // в SharedPreferences: любое изменение в пользовательском интерфейсе,
        // и даже вход в диалог Settings, будет приводить к вызовы callback,
        // см. onSharedPreferenceChanged
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // При завершении Activity необходимо отменять регистрацию
        // callback-функции обработки изменений в SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void makeGithubSearchQuery() {

        // Считываем введённое пользователем слово из названия репозитария
        String theWord = mSearchBox.getText().toString();

        // Запрашиваем список репозитариев, в названии которых есть указанное слово
        Call<SearchResponse> call = mClient.findRepos(theWord, "stars");

        // Выполняем запрос асинхронно и получаем положительный, либо негативный ответ
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                try {
                    // Запрос был успешным. Извлекаем полученные данные из response.body.
                    // Для накопления полученных данных используем StringBuilder
                    StringBuilder sb = new StringBuilder();
                    for (RepoDescription theRepo : response.body().items) {
                        sb.append(theRepo.full_name).append('\n');
                    }

                    // Если нам удалось загрузить данные посредством Web API, сохраняем
                    // их в LiveData - они будут доступны даже если Activity будет удалён,
                    // а потом повторно воссоздан
                    LiveDataRepository.getInstance().setData(sb.toString());
                }
                catch (NullPointerException ex) {
                    LiveDataRepository.getInstance().setData("No any data received");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Запрос не был выполнен, нужно обработать ошибку
                Log.i("Info", "Error");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Обрабатываем нажатие кнопки "Search"
        int itemThatWasClickedId = item.getItemId();
        if (R.id.action_search == itemThatWasClickedId) {
            makeGithubSearchQuery();
            return true;
        }

        // Обрабатываем нажатие кнопки "Settings"
        if(R.id.action_settings == itemThatWasClickedId) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Выполняем трассировку изменений в SharedPreferences
        if (key.equals(getString(R.string.pref_first_check_box))) {
            boolean firstCheckBox = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_first_check_box_default));
            Log.i("Info", "Successfully loaded first check box value: " + firstCheckBox);
        }
    }
}
