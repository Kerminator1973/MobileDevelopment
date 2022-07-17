package ru.kerminator.qt5research;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.qtproject.qt5.android.bindings.QtActivity;

public class MainActivity extends QtActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
    }

    static public String justGiveMeString(int value)
    {
        Log.i("ANDROID_STUDIO", "The method is called successfully");
        return "The static method is called";
    }

    public int justGiveMeInt()
    {
        // Вызываем метод C++ из Java-кода
        AndroidHelper.useMyNative("Hello, from the JAVA-method");

        Log.i("ANDROID_STUDIO", "We are in justGiveMeInt()");
        return 99;
    }
}
