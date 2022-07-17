#ifndef MYQMLPROXYCLASS_H
#define MYQMLPROXYCLASS_H

// В идеологии Qt, для обработки событий от компонентов QML в C++ коде
// используются proxy-классы

#include <QGuiApplication>
#include <QDebug>   // Заголовочный файл нужен для обеспечения возможности использовать qDebug()

#include <QAndroidJniEnvironment>
#include <QAndroidJniObject>


// Proxy-класс, через который обмениваются информацией
// два компонента пользовательского интерфейса (QML)
class MyQmlProxyClass : public QObject
{
    Q_OBJECT

public:
    void registerNativeMethods();

public:
    static void useMyNativeMethod(JNIEnv * env, jobject, jstring strParam);

public slots:

    void cppOnButtonClicked();
};

#endif // MYQMLPROXYCLASS_H
