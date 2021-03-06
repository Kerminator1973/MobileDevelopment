# Описание репозитария

В репозитарии храняться исходные тексты утилиты передачи информации из web-приложения кассиру системы АБС посредством SMS-сообщения. Утилита используется исключительно в демонстрационных целях на автономных стендах, которые не имеют доступа к SMS-шлюзам операторов мобильной связи. В промышленной эксплуатации, рассылка SMS осуществляется через корпоративную шину и адаптер SMS-шлюзов.

# Условия запуска приложения

Публикация приложения на Google Play не предусматривается. Установка осуществляется на телефон с разблокированным "режимом разработчика" средствами adb.

Для выполнения сетевых запросов Android-приложения используют TLS 1.2. **Express.js** поддерживает TLS 1.2. **Microsoft Internet Information Server** поддерживает TLS 1.2 начиная с Windows 8/Windows Server 2012, а TLS 1.3 с Windows 10/Windows Server 2016.

Схема работы предполагает использование трёх участников: 

1. Android-приложение, которое отправляет SMS через оператора мобильной связи
2. web-приложения на Node.js, которое используется для получения Android-приложением информации для отправки SMS
3. [POSTMAN](https://www.postman.com/) позволяет поместить в web-приложение данные для отправки

# Тестирование приложения

Для тестирования взаимодействия с серверным компонентом, используется приложение на Node.js + Express. Исходные тексты находятся в папке **server.js**.

Установка packages: `npm install`

Запуск: `node index.js`

Формат запросов к серверу: **GET**: https://127.0.0.1:3000/last, **POST** https://127.0.0.1:3000/send 

Примечание: в [POSTMAN](https://www.postman.com/) следует указать Заголовок: **Content-Type: application/json**, а в закладке **Body**: *raw* и *JSON*. Запросы отправляются на web-сервер строго по **https**.

Пример отправляемого запроса:

```json
{
	"phone": "123",
	"message": "Hello!"
}
```

В случае проблем при обеспечении взаимодействия с web-сервером, имеет смысл использовать **Logcat** в Android Studio. В Android-приложение был встроен **HttpLoggingInterceptor**, который сохраняет подробную информацию о сетевом взаимодействии в Logcat.

# Настройки приложения

При использовании эмулятора Android телефона, доступ к локальной машине осуществляется по адресу: `10.0.2.2` (loopback).

# Что нуждается в доработке

1. Настройка apiBaseUrl встроена в getInstance() сервиса, что встречается часто, но не совсем соответствует имени метода, т.к. мы не просто получаем instance, но и указываем, какой именно instance создавать
2. Запрос новых сообщений для отправки должен осуществляться в Android-сервисе, со временем жизни - больше, чем у Activity
3. Используется "счастливая" проверка сертификата
4. Нет проверки корректности введённого URL в SettingsActivity
5. Маска ввода не подключена к строкам редактирования в SettingsActivity
6. Адрес сервера и порт следовало бы объединить в одну настройку
7. API перегенерируется при любом изменении настроек в onSharedPreferenceChanged()
