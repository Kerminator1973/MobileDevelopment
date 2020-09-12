package ru.kerminator.githubsearch;

// Статья по применению шаблона проектирования Singleton:
// "Digesting Singleton Design Pattern in Java" by Keval Patel
// https://medium.com/@kevalpatel2106/digesting-singleton-design-pattern-in-java-5d434f4f322#.6gzisae2u

import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static String API_BASE_URL = "https://api.github.com/";

    private static volatile Retrofit retrofit = null;

    // Private constructor - блокируем возможность создать
    // новый экземпляр данного класса
    private ServiceGenerator() {

        // Предотвращаем возможность создания второго экземпляра класса
        // используя Java Reflection API
        if (retrofit != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Retrofit getInstance() {

        // Используем "Double check locking pattern", чтобы обеспечить
        // безопасность при многопоточном использовании singleton-а
        if (retrofit == null) {

            synchronized (ServiceGenerator.class) {
                if (retrofit == null) {

                    Log.i("Info", "Creating the Retrofit singleton");

                    // Создаём клиентский API, посредством которого можно
                    // будет выполнять запросы к REST API GitHub (v3)
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            );

                    retrofit = builder.client(httpClient.build()).build();
                }
            }
        }

        return retrofit;
    }
}
