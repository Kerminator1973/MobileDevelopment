# GitHubSearch
Это учебный проект, в рамках которого проводятся исследования: 
1. **Жизненный цикл Activity** (удаление и повторное создание (re-create) Activity при повороте экрана)
2. Интеграция в приложение библиотеки [Retrofit](https://square.github.io/retrofit/) с учётом жизненного цикла Activity. Создание singleton-сервиса
3. Использование **LiveData** для хранения загруженных (через WebAPI) данных. Это позволяет избежать негативных последствий удаления и повторного создания Activity (в частности, при повороте экрана)
4. Использование **PreferenceFragment** для создания Settings Activity почти без программирования

Информация о жизненном цикле Android-приложений была подчерпнута из бесплатного курса [Developing Android Apps by Google](https://classroom.udacity.com/courses/ud851) - см. Lesson 5: Lifecycle.

# Полезные практики
1. Все текстовые сообщения имеет смысл определять в **strings.xml**, для того, чтобы упростить локализацию (**i18n**) в дальнейшем
2. Для экономии экранного пространства, в простых формах, управляющие элементы можно выносить в заголовок приложения. Сделать это можно реализовав в Activity два перегруженных метода:

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	int itemThatWasClickedId = item.getItemId();
	if (itemThatWasClickedId == R.id.action_search) {
		makeGithubSearchQuery();
		return true;
	}
	return super.onOptionsItemSelected(item);
}
```

Кроме этого, нужно описать элемент, который будет добавляться в заголовок приложения (menu/main.xml):

```xml
<item
	android:id="@+id/action_search"
	android:orderInCategory="1"
	app:showAsAction="ifRoom"
	android:title="@string/search"/>
```

# Выбор типа Layout
Поскольку в данном примере есть FrameLayout, который занимает оставшуюся часть экрана и содержит внутри себя ScrollView,кажется разумным использовать **LinearLayout**. Используемый по умолчанию ConstraintLayout очень удобен в ситуациях, когда в Activity нет вложенных органов управления.

Чтобы разместить элемент, который займёт всё оставшееся пространство и позволит выполнять скроллирование внутри себя, следует воспользоваться следующей конструкцией:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" ...>
	
    <"ОРГАНЫ УПРАВЛЕНИЯ С ФИКСИРОВАННОЙ ПОЗИЦИЕЙ" />

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
		<СКРОЛЛИРУЕМЫЕ ОРГАНЫ УПРАВЛЕНИЯ />
        </ScrollView>
    </FrameLayout>
</LinearLayout>
```

# Re-create of an Activity
Причина, по которой Android удаляет Activity и пересоздаёт её при повороте экрана, состоит в том, что для горизонтального и вертикального режимов могут использоваться принципиально разные Layouts.

При этом, Android умеет автоматически сохранять значения введённые в органах управления типа **EditText**, но не сохраняет контент, динамически добавленный в **TextView**. Приведенный код подтверждает подобное поведение экспериментально. Более того, восстановление введённых в строках редактирования значений осуществляется уже после выполнения **onCreate()**, что подчеркивает особую важность параметра **savedInstanceState**, используемого для восстановления состояния Activity при re-create после удаления. 

Например, при нажатии командной кнопки можно сохранить введённые/вычисленные значение в Bundle и запустить некоторую асинхронную задачу. В более ранних API (до 28) для подобных целей использовались Loader-ы (см. LoaderManager и AsyncTaskLoader), но затем Google объявила их *depricated* и рекомендует использовать **LiveData**<>. Тем не менее, для иллюстрации механизма сохранения данных между удалением и пересозданием Activity, привожу пример с LoadManager:

```java
Bundle queryBundle = new Bundle();
queryBundle.putString(SEARCH_QUERY_URL_EXTRA, githubSearchUrl.toString());

LoaderManager loaderManager = getSupportLoaderManager();
Loader<String> githubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
if (githubSearchLoader == null) {
    loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
} else {
    loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
}
```

При пересоздании Activity, созданный Bundle будет передан в метод onCreate():

```java
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState != null) {
        String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
        ...
    }
```

Таким образом, можно говорить о том, что Bundle - это *безопасное место для хранения данных в беспокойном Android Lifecycle*.

# Подключение библиотеки Retrofit (от Square)
Библиотека [Retrofit2](https://square.github.io/retrofit/) используется для организации сетевого взаимодействия по HTTP(s). Wiki по проекту: https://github.com/square/retrofit/wiki

Основные бенефиты использования Retrofit: код гораздо более компактный, понятный, его цикломатическая сложность ниже, его проще сопровождать и он является более надёжным. Retrofit 2 генерирует класс, выполняющий синхронное/асинхронные запросы на сервер по предоставленному программистом описанию интерфейса.

[Обучающие видео](https://futurestud.io/tutorials/retrofit-2-basics-of-api-description) по Retrofit доступны на YouTube в группе [Future Studio](https://www.youtube.com/c/FutureStudio).

[Инструкция](https://futurestud.io/tutorials/retrofit-getting-started-and-android-client) о добавлении функционала получения данных по протоколу https.

Добавить Retrofit в проект можно включив следующие зависимости (/app/build.gradle – «Module:app»):

```
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.6.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.2'
}
```

**GSon** – это конвертор из JSON в java-объект и обратно. После добавление зависимостей, необходимо в Android Studio выполнить синхронизацию проекта (**Sync Now**).

Для разрешения доступа в интернет, необходимо добавить соответствующую настройку в «AndroidManifest.xml»:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Следующим этапом необходимо описать REST API, указывая относительный URI, http-verb и описание входных и выходных параметров. Для REST API сайта GitHub.com, таким описанием может быть "GitHubService.java" (подробности см. в коде в репозитарии):

```java
class RepoDescription {
    String node_id;
    String name;
    String full_name;
    String url;
}

class SearchResponse {
    ArrayList<RepoDescription> items;
    int total_count;
}

public interface GitHubService {
    @Headers("Content-Type: application/json")
    @GET("/search/repositories")
    Call<SearchResponse> findRepos(
            @Query("q") String q,
            @Query("sort") String sort
    );
}
```

В простейшем случае, создать объект-клиент для выполнения запросов к REST API можно в классе-Activity:

```java
public class MainActivity extends AppCompatActivity {

    GitHubService mClient;
    public static String API_BASE_URL = "https://api.github.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder.client(httpClient.build()).build();
        mClient =  retrofit.create(GitHubService.class);
    }

    private void makeGithubSearchQuery() {
        Call<SearchResponse> call = mClient.findRepos("hello", "stars");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                Log.i("Info", "OK");
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.i("Info", "Error");
            }
        });
    }
```

В промышленном коде нужно учитывать, что Activity удаляется и пересоздаётся постоянно и лучше всего создать специальный сервис в соответствии с шаблоном проектирования **singleton** из статьи Keval Patel [Digesting Singleton Design Pattern in Java](https://medium.com/@kevalpatel2106/digesting-singleton-design-pattern-in-java-5d434f4f322#.6gzisae2u). Использование Singleton-сервиса позволят избежать чрезмерного расхода вычислительных ресурсов при пересоздании Activity.

# Использование LiveData

В новых версиях API для того, чтобы избежать потери данных при пересоздании Activity рекомендуется использовать шабллонный класс LiveData<>. В простейшем случае, разрабатывает класс singleton владеющий экземпляром объекта LiveData<>. Пример:

```java
public class LiveDataRepository {

    private static LiveDataRepository instance;
    
    private LiveDataRepository() {}

    public static LiveDataRepository getInstance() {
        if (instance == null) {
            instance = new LiveDataRepository();
        }
        return instance;
    }

    private static MutableLiveData<String> liveData = new MutableLiveData<>();

    LiveData<String> getData() { return liveData; }
    void setData(String value) { liveData.setValue(value); }
}
```

В этом singleton-объекте есть два метода - один из них позволяет изменить данные, а второй получить объект LiveData<>, который можно использовать в шаблоне Observer. Вот как может выглядет подписка на события изменения данных:

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveData<String> liveData = LiveDataRepository.getInstance().getData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
	    	...
                // Получаем данные посредством LiveData и помещаем в строку редактирования
                mGitHubSearchResult.setText(value);
            }
        });
    }
```

При любом изменении данных, либо при пересоздании Activity метод **onChanged()** будет вызван.

Изменять данные можно, например, в обработчике успешного считывания данных из WebAPI с использованием Retrofit:

```java
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
		...
                LiveDataRepository.getInstance().setData(strList);
            }
```

Моя реализация носит упрощённый характер и **не соответствует шаблону проектирования MVVM**, но моя задача - снизить порог вхождения в тему, а не продемонстрировать знание шаблона проектирования. Рекомендую для ознакомления следующую [статью на русском языке](https://startandroid.ru/ru/courses/architecture-components/27-course/architecture-components/525-urok-2-livedata.html) и [статью о MVVM + Retrofit на английском](https://medium.com/@amtechnovation/android-architecture-component-mvvm-part-1-a2e7cff07a76).

# Использование PreferenceFragment для создания Settings
В Android SDK существует возможность создать Activity с настройками приложения, практически, без программирования. Такая Activity может использовать один, или несколько PreferenceScreen. Пример описания настраиваемого свойства:

```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <CheckBoxPreference
        android:defaultValue="true"
        android:key="show_something"
        android:summaryOff="Hidden"
        android:summaryOn="Shown"
        android:title="Show Something" />
</PreferenceScreen>
```

Экраны свойств встраиваются в описание Settings Activity:

```xml
<?xml version="1.0" encoding="utf-8"?>
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/activity_settings"
    android:name="ru.kerminator.githubsearch.SettingsFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Связующим элементом является класс SettingsFragment, который был указан в поле "name" (ru.kerminator.githubsearch.SettingsFragment):

```java
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_githubsearch, rootKey);
    }
}
```

Приведённый выше класс наследуется от **PreferenceFragmentCompat**, а при вызове метода onCreatePreferences() устанавливает поля свойств приложения из файла ресурсов (xml). 

В идеальной ситуации, дальнейшее редактирование свойств приложения не требует программирования, но на практике это не так. В случае, если мы добавляем любой Preference отличный от checkbox, имеет смысл реализовать поддержку Summory - поясняющего текста, отражающего текущее состояние свойства. Поскольку checkbox имеет только для значения (on и off) для этого типа Preferences поддержка summary выполняется автоматически. Кроме этого, необходимо добавить код, который будет проверять корректность значений введённых в поле **EditTextPreference**. Если этого не сделать, то в SharedPreferences может быть сохранен мусор и это может привести к падению приложения сразу же на старте, т.е. ещё до того, как появится возможность изменить ошибочно введённые значения. В обоих случаях код следует добавлять в "SettingsFragment.java".


