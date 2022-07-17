#include <QtGlobal>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QJsonValue>
#include "myhttpservice.h"
#include "myqmlproxyclass.h"


void MyQmlProxyClass::cppOnButtonClicked() {
    qDebug() << "Called the C++ slot without message";

    auto manager = CMyHttpService::getManager();

    connect(manager, &QNetworkAccessManager::finished, this, &MyQmlProxyClass::replyFinished);

    // TODO: перенести функционал в CMyHttpService

    // Выполняем GET-запрос
    QNetworkRequest request;
    request.setUrl(QUrl("https://restcountries.eu/rest/v2/all"));

    // В простейшем случае можно не использовать SSL, но такое решение не подходит
    // для промышленных приложений
    //request.setUrl(QUrl("http://flagpedia.net/"));

    // Для успешного подключения с использованием SSL/TLS необходимо
    // сконфигурировать подключение, в том числе, настроить сертификаты.
    // Также необходимо добавить динамические библиотеки из состава OpenSSL.
    // Их можно собрать самостоятельно из исходных текстов, либо загрузить
    // из доверенного источника (я использовал - https://indy.fulgan.com/SSL/).
    // Необходимо учитывать разрядность библиотек (x64, x32). Названия требуемых
    // библиотек для Windows: libeay32.dll, ssleay32.dll
    request.setSslConfiguration(QSslConfiguration::defaultConfiguration());

    QNetworkReply *networkReply = manager->get(request);

    // Добавляем ещё несколько обработчиков событий, но уже связанных
    // с QNetworkReply, а не с QNetworkAccessManager

    // Обработка ошибки при возникновении сетевого сбоя, например, невозможности
    // соединения с хостом

    // Раньше, до Qt 5.15, использовался следующий код:
    // connect(networkReply, QOverload<QNetworkReply::NetworkError>::of(&QNetworkReply::error),
    //    this, &MyQmlProxyClass::networkReplyError);
    // В Qt 5.15 можно получить сообщение об ошибке: 'error' is deprecated:
    // Use QNetworkReply::errorOccurred(QNetworkReply::NetworkError) instead
    // Собирается и работает вот такой код:
    connect(networkReply, &QNetworkReply::errorOccurred, this, &MyQmlProxyClass::networkReplyError);

    // В теории, необходимо обрабатывать загруженные документы в слоте readyRead,
    // но на практике это можно делать и в слоте replyFinished
    // connect(networkReply, &QNetworkReply::readyRead, this, &MyQmlProxyClass::readyRead);

    // Сигнал QNetworkReply::finished может быть использован для обработки
    // полученных результатов
    // TODO: разобраться, в чём разница между QNetworkAccessManager::finished
    // и QNetworkReply::finished.
    // Для примера может быть использовано лямбда-выражение, а не отдельный метод
    QObject::connect(networkReply, &QNetworkReply::finished, [=]()
    {
        qDebug() << "We have got some headers:";

        QList<QPair<QByteArray, QByteArray>> responses = networkReply->rawHeaderPairs();
        qDebug() << responses;
    });
}

// В данной реализации - не актуально
// void MyQmlProxyClass::readyRead(QNetworkReply *reply) {
//}

void MyQmlProxyClass::networkReplyError(QNetworkReply::NetworkError errorCode)
{
    qDebug() << "MyQmlProxyClass::requestError: " << errorCode;
}

void MyQmlProxyClass::replyFinished(QNetworkReply *reply) {

    if(reply->error()) {

        qDebug() << "The error happened during the request";

    } else {

        qDebug() << "The data was received";

        // Указываем на необходимость последующего удаления памяти,
        // зарезервированной для хранения http-запросов
        reply->deleteLater();

        // Обрабатываем ответ сервера
        if(!reply->error()) {

            // TODO: Здесь имеет смысл обработать HTTP Status Codes,
            // например, так:
            //
            // if (reply->error() == QNetworkReply::OperationCanceledError) {}
            // else if (reply->error() > 400 && reply->error() < 500) {}
            // else if (reply->error() == QNetworkReply::AuthenticationRequiredError) {}
            // else if (reply->error() == QNetworkReply::HostNotFoundError || reply->error() == QNetworkReply::ConnectionRefusedError)

            QByteArray receivedData = reply->readAll();
            if (receivedData.isEmpty())
            {
                qDebug() << "Empty JSON data";
                return;
            }

            QJsonParseError e;
            QJsonDocument d = QJsonDocument::fromJson(receivedData, &e);
            if (!d.isNull()) {
                if (d.isEmpty())
                    qDebug() << "Получены пустые данные";

                if (d.isObject())
                    qDebug() << "Получен объект";

                if (d.isArray()) {

                    qDebug() << "Получен массив";

                    QJsonArray array = d.array();
                    for (const QJsonValue v : array) {
                        qDebug() << "name: " << v.toObject()["name"].toString();

                        // TODO: через сигнал можно добавить полученные данные в QML-форму
                    }
                }
            }
        }
    }
}
