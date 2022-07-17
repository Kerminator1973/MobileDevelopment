#include "myhttpservice.h"


// Инициализация экземпляра класса будет осуществляться при первом
// использовании. Это произойдёт, когда из производного классса будет
// вызван метод getManager()
QNetworkAccessManager* CMyHttpService::m_pManager = nullptr;

const QString CMyHttpService::apiAddress = "https://restcountries.eu/rest/v2/all";


CMyHttpService::CMyHttpService(QObject *parent) : QObject(parent)
{

}
