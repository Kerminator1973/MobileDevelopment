package ru.kerminator.sms_transmitter;

// Статья по применению шаблона проектирования Singleton:
// "Digesting Singleton Design Pattern in Java" by Keval Patel
// https://medium.com/@kevalpatel2106/digesting-singleton-design-pattern-in-java-5d434f4f322#.6gzisae2u

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    // Экземпляр класса OkHttpClient отвечает за коммуникацию
    // посредством HTTP/HTTPS

    // Существует два способа организовать SSL/TLS-канал:
    // 1) Для демонстрационных целей - можно использовать компонент проверки
    //      сертификатов, который доверяет всем (UnsafeOkHttpClient).
    //      Этот режим также называется Trust Everyone
    // 2) Для работы в Production - следует доверять сторонам, сертификаты которых
    //      могут быть подтверждены. Параметры настройки должны быть
    //      быть размещены в папках с ограниченным доступом на сервере сборки

    // TODO: необходимо учитывать среду сборки проекта:
    // - для Dev/Test использовать UnsafeOkHttpClient
    // - для Production использовать обязательную проверку TLS сертификатор
    private static OkHttpClient okHttpClient =
            ru.kerminator.sms_transmitter.UnsafeOkHttpClient.getUnsafeOkHttpClient();

    // TODO: загружать параметр из SharedPreferences.
    // TODO: базовый адрес должен был бы быть "https://10.0.2.2:3000/", но для
    // простоты тестирования кода настройки строки подключения он был выбран
    // заведомо ошибочным
    private static String API_BASE_URL = "https://127.0.0.1:8080/";

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

    public static Retrofit getInstance(String apiBaseUrl) {

        // TODO: У метода getInstance() не должно быть параметра URL, но
        // он был добавлен для настройки URL при запуске Activity. Нужно
        // придумать другую, более разумную схему
        API_BASE_URL = apiBaseUrl;

        // Используем "Double check locking pattern", чтобы обеспечить
        // безопасность при многопоточном использовании singleton-а
        if (retrofit == null) {

            synchronized (ServiceGenerator.class) {
                if (retrofit == null) {

                    Log.i("Info", "Creating the Retrofit singleton");

                    // Создаём клиентский API, посредством которого можно
                    // будет выполнять запросы к API
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            );

                    retrofit = builder.client(okHttpClient).build();
                }
            }
        }

        return retrofit;
    }

    // Метод позволяет заменить используемый API указанным
    public static void changeApiBaseUrl(String newApiBaseUrl) {
        API_BASE_URL = newApiBaseUrl;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );

        retrofit = builder.client(okHttpClient).build();
    }
}
