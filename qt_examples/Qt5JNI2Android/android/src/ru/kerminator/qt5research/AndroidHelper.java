package ru.kerminator.qt5research;

// Класс AndroidHelper выполняет вспомогательную функцию, что следует из
// его названия. Экземпляр этого класса создаётся в С++ коде registerNativeMethods()
// с целью связывания native-методов (Java) с их реализацией на C++

public class AndroidHelper {

    // Регистрируем native-вызов (C++)
    public static native void useMyNative(String someValue);
}
