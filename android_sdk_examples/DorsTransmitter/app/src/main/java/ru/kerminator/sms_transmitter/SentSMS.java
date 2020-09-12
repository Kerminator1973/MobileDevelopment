package ru.kerminator.sms_transmitter;

public class SentSMS {

    String phone;
    String message;

    SentSMS(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }
}
