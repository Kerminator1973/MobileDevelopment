#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QDebug>   // Заголовочный файл нужен для обеспечения возможности использовать qDebug()
#include "MyQmlProxyClass.h"

int main(int argc, char *argv[])
{
    QCoreApplication::setAttribute(Qt::AA_EnableHighDpiScaling);

    QGuiApplication app(argc, argv);

    QQmlApplicationEngine engine;
    const QUrl url(QStringLiteral("qrc:/main.qml"));
    QObject::connect(&engine, &QQmlApplicationEngine::objectCreated,
                     &app, [url](QObject *obj, const QUrl &objUrl) {
        if (!obj && url == objUrl)
            QCoreApplication::exit(-1);
    }, Qt::QueuedConnection);
    engine.load(url);

    //
    QObject *wholeWindow = engine.rootObjects().first();

    // Создаём Proxy-класс для обработки сигналов QML-кода.
    MyQmlProxyClass myQMLProxyClass;

    //
    myQMLProxyClass.registerNativeMethods();

    // Привязываем QML-кнопку к обработчику на C++
    qDebug() << "The second connection complete status is: " <<
        QObject::connect(wholeWindow, SIGNAL(qmlCallJNISignal()),
        &myQMLProxyClass, SLOT(cppOnButtonClicked()));

    // Регистрируем функции, которые можно будет вызвать из Java-кода

    return app.exec();
}
