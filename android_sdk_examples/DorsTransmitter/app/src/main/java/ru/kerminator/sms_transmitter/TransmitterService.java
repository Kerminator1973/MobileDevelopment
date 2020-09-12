package ru.kerminator.sms_transmitter;

import retrofit2.Call;
import retrofit2.http.GET;


// Класс описывает ответ сервера
class SMSResponse {
    String phone;
    String message;
}

// Описание интерфейса web-сервиса
public interface TransmitterService {

    // Запрос сообщения, которое нужно передать по SMS
    //@Headers("Content-Type: application/json")
    @GET("/last/")      // Для ASP.NET Core 3: "/admev/handler=SMS"
    Call<SMSResponse> getLast();
}
