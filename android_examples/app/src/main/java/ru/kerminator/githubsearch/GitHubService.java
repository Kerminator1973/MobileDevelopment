package ru.kerminator.githubsearch;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


class RepoDescription {
    String node_id;
    String name;
    String full_name;
    String url;
}

// Класс описывает ответ сервера GitHub на поисковый запрос.
// Получить состав полей ответа можно используя Postman
class SearchResponse {
    ArrayList<RepoDescription> items;
    int total_count;
}

// Описание интерфейса web-сервиса. Документация по API: https://developer.github.com/v3/search/
public interface GitHubService {

    // Строка запроса поиск репозитария GitHub по его имени.
    // Параметры:
    //  q - имя (возможно, частичное) репозитария
    //  sort - сортировка (чаще всего используется значение "stars")
    @Headers("Content-Type: application/json")
    @GET("/search/repositories")
    Call<SearchResponse> findRepos(
            @Query("q") String q,
            @Query("sort") String sort
    );
}
