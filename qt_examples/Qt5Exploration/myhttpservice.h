#ifndef CMYHTTPSERVICE_H
#define CMYHTTPSERVICE_H

#include <QObject>
#include <QNetworkAccessManager>


// Заметки по архитектуре приложения:
//      Создавать отдельный базовый класс, обеспечивающий
//  использование QNetworkAccessManager имеет смысл в том случае,
//  если существует потребность исключить дублирование кода
//  обработчиков событий, например, для обработки ошибок, или
//  добавлении логирования, а также для настройки TLS-соединения

class CMyHttpService : public QObject
{
    Q_OBJECT
public:
    explicit CMyHttpService(QObject *parent = nullptr);
     virtual ~CMyHttpService() {}

signals:
public slots:

private:
    // Единственный экземпляр класса QNetworkAccessManager (QNAM)
    // используется для отправки и получения асинхронных запросов через сеть.
    // Работа с классом подробно описана в книге "Hands-on Mobile and Embedded
    // Development with Qt 5" by Lorn Potter
    static QNetworkAccessManager* m_pManager;

    // Строка, в которой определён URI-адрес используемого REST API
    static const QString apiAddress;

public:

    // Метод getManager() реализует шаблон проектирования singleton - который
    // реализует отложенное создание экземпляра класса (при необходимости), а
    // также существование единственного экземпляра этого класса
    static QNetworkAccessManager* getManager()
    {
        if (m_pManager == nullptr)
        {
            m_pManager = new QNetworkAccessManager();
        }
        return m_pManager;
    }
};

#endif // CMYHTTPSERVICE_H
