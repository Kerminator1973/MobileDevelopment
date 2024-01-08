## Разработка мобильных приложений

Вне зависимости от используемой технологии (Android Studio/Xcode, React Native/Flutter, Qt, Xamarin), существуют общие принципы разработки мобильных приложений.

Начинать разработку приложения следует с планирования навигации, учитывающей модель информационной безопасности, хранилища состояния приложения и определения API для взаимодействия с внешними серверными компонентами.

## Навигация

Современное приложение состоит из ряда отдельных экранов (Activities), переходы между которыми позволяют получать доступ к функциям приложения. Экраны группируются некоторым способом. Под навигацией подразумеваются переходы как внутри группы, так и между группами.
Чаще всего применяются три навигационных компонента: Side Drawer, Stack и Bottom Tab (или просто Tab).

**Stack Navigator** определяет связь, в которой есть родительские и дочерние экраны. Дочерние экраны всегда располагаются поверх родительских и это обеспечивает возможность можно вернуться к родительскому экрану (например, используя кнопку «Back»). В Header-е Stack-а часть размещается иконка "Back" для возврата к предыдущему экрану (с поясняющим текстом), а также дополнительные иконки-кнопки, такие как "Confirm" и "Save."

**Tab Navigator** определяет группу экранов одного уровня между которыми можно свободно перемещаться посредством жеста swipe, либо щелчком на закладку (Tab).

**Side Drawer** определяем меню верхнего уровня, позволяя выполнить переходы на функционально не связанные, либо слабо-связанные экраны. Обычно именно Side Drawer является компонентом верхнего уровня.

Также может применяться **Switch Navigator** – особый тип экрана, который проверяет, была ли выполнена аутентификация пользователем и в зависимости от результата, перенаправляет пользователя в главное меню, либо на экран аутентификации. Впрочем, в React Navigation v5 компонент Switch Navigator уже не используется.

## Хранилище состояния

Состояние – это любые данные, которые разделяются между различными экранами, из которых состоит приложение. Состояние может хранить *JSON Web Token* для доступа к внешним ресурсам, список валют, которыми оперирует приложение, корзина с товарами, и т.д. Данные в хранилище сохраняются при переходе от экрана к экрану.

Часто, работа с хранилищем предполагает возникновение некоторого действия пользователя, которое транслируется в слой бизнес логики, сохраняется глобально, а затем сообщение об изменении транслируется подписчиках. Например, в модели **Redux Store** действие пользователя приводит к возникновению действия (**Action**), которое может выполнить асинхронный запрос на сервер (например, с использованием [Redux-Thunk](https://github.com/reduxjs/redux-thunk)), результат действия попадает в **Reducer**-ы (при вызове функции **dispatch**()), каждый из которых может зафиксировать результат, или часть результата в свой контейнер данных, а затем подписчики (экраны) получают сообщение об изменении данных в хранилище, посредством хука **useSelector**().

Можно считать, что глобальное хранилище это и Model, и Controller в модели MVC – оно выполняет некоторую логику и хранит данные не зависимо от представления (View).

В React Native чаще всего применяется компонент [Redux Store](https://github.com/reduxjs/react-redux), но его постепенно заменяет [Context API](https://ru.reactjs.org/docs/context.html), который считается более простым для понимания и более совершенным.

## Аутентификация пользователей
 
Предполагается, что при успешной аутентификации пользователя, Back-End генерирует [Json Web Token](https://jwt.io/). Важно применить разумную стратегию управления временем жизни токена безопасности. Ключевые вопросы:

1. Каким должно быть время жизни токена
2. В какой момент должен быть выдан новый токен
3. Нужно ли хранить токен в постоянном хранилище и проверять его валидность до аутентификации пользователя при повторном запуске приложения
4. Каким образом следует обрабатывать сообщения о истечении времени жизни токена
5. Нужно ли оценивать валидность токена на мобильном устройстве (токен выдаётся сервером)
 
## Пользовательский интерфейс

Для мобильных телефонов определены специальные «правила хорошего тона», к которым, например, относится *pull to update* – жест «перемещение пальца с верхней части экрана вертикально вниз» приводит к обновлению данных в списке (они повторно загружаются с сервера).

Для обеспечения удобного доступа к органам управления, большинство форм поддерживает скроллирование.

Для того, чтобы избежать перекрытия строки ввода, или другого органа управления системными элементами (чёлка, камера) используются специальные вспомогательные компоненты, такие как **SafeAreaView** в React Native.

## Доступ к Native-функциями

По мере появления новых возможностей в операционных системах, возникает необходимость доступа к этим функциями из «*not native code*». Например, для управления вибрацией и работой с биометрическим сенсором может потребоваться доступ к Native API.

В случае Qt, доступ к Android API осуществляется посредством **Java Native Interface**.

### Специализированные протоколы

Потенциально, большей проблемой для гибридных приложений (React Native, Flutter, Qt), чем доступ к Native Functions операционной системы, может являеться возможность использования специализированных протоколов асинхронного взаимодействия, таких как [SignalR](https://docs.microsoft.com/ru-ru/aspnet/core/signalr/java-client?view=aspnetcore-3.1) и [gRPC](https://grpc.io/docs/quickstart/android/). Для обоих протоколов есть библиотеки для Java-клиента, которые работают "из коробки". Для обоих протоколов есть прекрасные JavaScript-реализации для использования в браузере, но далеко не факт, что эти библиотеки можно использовать без адаптации в React Native. Вопрос их поддержки в Flutter и Qt - отдельная исследовательская задача.

## Серверные serverless технологии

Во многих случаях, разработчикам мобильных приложений не требуется сложный backend. Может оказаться, что вполне достаточно возможности аутентификации пользователей, возможности сохранить какие-то структрурированный данные в базу (иногда достаточно пары ключ-значение), а также сохранения данных в файловое хранилище (типа S3). В подобных ситуациях разрабатывать специализированное серверное решение может оказаться изыточным.

Решением задачи может быть **serverless backend**. Наиболее типовыми решениями являются: [Google Firebase](https://firebase.google.com/), [Parse Server](https://parseplatform.org/), [AWS Lambda](https://aws.amazon.com/ru/lambda/).

Для Firebase реализовано множество библиотек подключения к серверу для разных инструментальных средств. Ещё одна исключительно важная особенность - интеграция с Google Analytics, что позволяет, например, транслировать из приложения в облако информацию о возникших ошибках в приложении, что влияет на оперативность поиска и устранения сбоев.

Parse Server - разработка, которая была куплена Facebook, но затем "отпущена" в open-source. В [AWS](https://aws.amazon.com/marketplace/pp/Bitnami-Parse-Server-Certified-by-Bitnami/B01BLQ17TO) есть образ Parse Server от [Bitnami](https://bitnami.com/), который может быть развёрнут в облаке AWS за пару минут.

AWS Lambda - промежуточный уровень, описываемый как набор функций в облаке AWS, разработанных на разных языках программирования и подходит для потоковой обработки данных.

Похожая на Firebase платформа с поддержкой GraphQL- [8Base](https://www.8base.com/). Доступна [статья](https://www.8base.com/blog/5-reasons-why-developers-are-choosing-8base-over-firebase) о сравнении Firebase и 8Base.

### Основные проблемы serverless backend

К основным проблемам можно отнести REST API и NoSQL.

REST API создаёт жёсткие связи требуя разные endpoints (шаблоны http-запросов) для разных запросов. Если используются разные команды для разработки front-end и back-end, то необходимо координировать любые изменения. Ответы на запросы в REST API часто содержат избыточные данные.

GraphQL имеет ряд преимуществ, например, допускает любые запросы и *mutations* в endpoint. Запросы формируются на клиентской стороне и, следовательно, зависимость от разработчиков back-end значительно снижена. GraphQL возвращает только те данные, которые действительно нужно при выполнении конкретной операции.

NoSQL действительно работают очень быстро при получении данных, но у NoSQL есть огромные проблемы с поддержкой релационной модели и многие вещи, такие как группировка с коррелирующими запросами в NoSQL выполнить нельзя. Для бизнес-проектов это представляет существенный риск.

## Лучшие/худшие свойства инструментальных средств

Существует два альтернативных подхода к разработе мобильных приложений: использование *Native Tools* и языков *Native Ecosystems* и гибридные приложения, в которых используется слой, обеспечивающий независимость от конкретной эко-системы.

Ниже приведена сводная таблица по наиболее распространённым технологиям разработки мобильных технологий:

|       Тип      |     Язык    | Среда разработки |
|:--------------:|:-----------:|:----------------:|
| Android Native | Kotlin/Java |  Android Studio  |
| iOS Native     |    Swift    |       Xcode      |
| React Native   |  JavaScript |   VSCode, Expo   |
| Flutter        |     Dart    |  Android Studio  |
| Qt             |   C++/QML   |     QtCreator    |
| Xamarin        |   C#/XAML   |   Visual Studio  |
| Avalonia UI    |   C#/XAML   |      VSCode      |

Считается, что гибридные приложения работают чуть медленнее, чем нативные, а также используют не все возможности платформы. Тем не менее, использование гибридных платформ имеет ряд преимуществ.

В случае, если осуществляется разработка под две основные платформы (Android/iOS) с использованием Native инструментальных средств, чаще всего необходимо две специализированные команды, каждая из которых обладает навыками в разработке ПО для конктретной платформы. Две команды означает, как минимум, двухкратное увеличение затрат на разработку и отставание одной из команд в добавлении функций. Ввиду того, чтоб каждый из платформодержателей выпускает значительные обновления основных SDK, как минимум, раз в год на мероприятиях [Apple WWDC](https://developer.apple.com/wwdc20/), [Google I/O](https://developers.google.com/events), крайне слолжно поддерживать актуальный уровень знаний в головах разработчиков только одной команды.

Инструментальные средства для разработки гибридных приложений (React Native, Flutter, Qt, Xamarin) позволяют вести разработку приложений для основных платформ одной командой, жертвуя: производительностью кода и некоторой задержкой с поддержкой новых SDK. При этом затраты на поддержку новых SDK берёт на себя community, а не команда разработки приложения. Соответственно, стоимость разработки приложений снижается кратно.

Кроме этого, на некоторых платформах есть дополнительные инструменты, которые позволяют актуализировать состояние кодовой базы в полностью автоматическом режиме. Примером такого инструментария является [DependaBot](https://github.com/dependabot), контролирующий и корректирующий зависимости от packages, при нахождении уязвимостей в них.

Выбор языка программирования так же имеет огромное значение, особенно в том случае, если команда самостоятельно разрабатывает Back-End. Так, например, использование JavaScript для разработки и Front-End, и для Back-End, позволяет легче перераспределять специалистов между командами, т.к. они используют почти один и тот же инструментарий. Применение C#, например, даёт доступ к LINQ, который позволяет значительно упростить код обработки данных, оперируя конструкциями, похожими на SQL, для любых контейнеров данных.

Следует обратить внимание на тот факт, что общий подход к разработке пользовательского интерфейса в большинстве фреймворков - композиция элементарных, базовых компонентов. Этот подход обусловлен необходимостью поддержки разных платформ (iOS, Android). Такой подход носит ограничивающий характер и, например, критически затрудняет встраивания необычных эффектов, таких как 3D-анимация. Исключением является, разве что Qt, в котором поддержка 3D-эффектов относится к базовым свойствам render-а QML.

### Android Studio

Очень удобный IDE. Превосходные возможности отладки кода. IDE позволяет решить, буквально, любые задачи - от локализации до анализа производительности. Прекрасная реализация IntelliSense.

Можно выбрать основной язык программирования: Java/Kotlin.

Высокропроизводительный код можно разработать на C++ 14/17 (но это встречается не часто).

Великоленый выбор вспомогательных библиотек: [Square Retrofit](https://square.github.io/retrofit/), [SignalR](https://docs.microsoft.com/ru-ru/aspnet/core/signalr/java-client?view=aspnetcore-3.1).

Порог вхождения - очень высокий. Количество работы выполняемое вручную - большое.

Бурное развитие Android SDK привело к тому, что существует множество схожих инструментов и подходов, которые обладают разными ограничениями и разобраться в их отличиях крайне тяжело. Например, актуальным способом разметки пользовательского интерфейса является [ConstraintLayout](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout), а другие виды разметки, такие как LinearLayout и RelativeLayout могут негативно влиять на плавность пользовательского интерфейса, особенно при наличии большого количества уровней вложенности. Вместе с тем, существует множество материалов, в которых используются именно LinearLayout и RelativeLayout. Это создаёт проблему в выборе актуального инструмента - разрабочик, чаще всего не знает, какую из альтернатив следует выбрать. Как результат, очень высок риск выбора плохого, или худшего инструмента для реализации бизнес-критичного функционала. Из-за огромного количества альтернатив, разумный выбор можно сделать только при наличии огромного профессионального опыта. Ситуацию ухудшает тот факт, что в некоторых ситуациях без использования устаревших альтернатив не обойтись. Примеры альтернатив:

1. Java vs Kotlin
2. ConstraintLayout vs LinearLayout vs RelativeLayout
3. LiveData vs AsyncTask

### Qt/QML

Ориентирован на разработчиков C++.

Потенциально - очень высокая скорость скомпилированного C++ 14/17 кода. QML разработан с использованием OpenGL, т.е. работа тоже должна быть очень производительной.

Явным образом добавляется сборка **openSSL**, что позволяет утверждать, что в Qt-приложениях значительно меньше сетевых уязвимостей, чем в мобильных приложениях разработанных с использованием других инструментальных средств. Это особенно актуально для банковских приложений и приложений для работы с криптовалютами.

Большой набор вспомогательных библиотек: работа с hardware, WiFi/Bluetooth, SQLite, и т.д. Возможно включение в пользовательский интерфейс 3D-графики, что позволяет создавать уникальный пользовательский опыт.

Интеграция кода C++ с Java/Swift работает надёжно, но [требует использования](https://github.com/Kerminator1973/Qt5JNI2Android) **JNI** (для Android). В худшем случае, приложение требует хороших навыков программирования на C++, Java/Kotlin/Swift и JavaScript.

В случае запуска на микропроцессорах с разной архитектурой (x86 и ARM - сейчас, а в будущем - ARM, RISC-V, MIPS) - нужно делать bundle под каждую платформу, либо super-bundle, в котором есть скомпилированный код для всех платформ.

Сложность разработки и поддержания инструментария в работоспособном состоянии - высокая. При выпуске нового Android SDK, периодически, нарушаются настройки целевой системы (Android SDK, NDK, JDK), восстановление работоспособности которой может быть сложной технической задачей. Лучше всего работают "автоматически распознанные" конфигурации, т.е. при обновлении Android SDK может потребоваться дождаться соответствующего обновления Qt и переустановки системы с запуском утилиты автоматического распознавания конфигураций.

Основные недостатки Qt как платформы: относительно маленькое community (в основном - коммерческие пользователи, которые мало, что публикуют как open source), а также сложная лицензионная политика, которая требует, как минимум, привлечения юриста, чтобы разобраться с тем, за какой функционал нужно платить, а какой является бесплатным, с учётом лицензии, под которой публикуется разработанный программный продукт.

Пример приложения для Android с [QML и https](https://github.com/Kerminator1973/Qt5Exploration).

Статья об [установке Qt](qtinstall.md).

### React Native

Низкий порог вхождения на React-программистов и программистов для которых JavaScript-родной. Порог вхождения для программистов на практикующих JavaScript - высокий.

Парадигма программирования очень похожа на разработку web-приложения на React.

Огромное количество готовых компонентов.

Удобный вспомогательный инструментарий, например, "горячая" перезагрузка приложения в эмуляторе при изменении кода - эта возможность доступна в [Expo](https://expo.io/).

При использовании **Expo Managed Workflow**, дистрибьюция приложений возможна без оплаты аккаунта разработчика Google Play Market, или Apple AppStore. Разработка под iOS возможна без компьютера с macOS и без iPhone, см.: [Snack](https://snack.expo.io/).

Высокая скорость разработки пользовательского интерфейса, но инфраструктурые задачи (например, использование SQLite, или многопоточность) - в зачаточном состоянии.

Доступны библиотеки для построения различных графиков. Мощнейшая и крайне популярная библиотека [D3.js](https://d3js.org/) может быть использована в React Native, но это связано с [некоторыми ограничениями](https://hswolff.com/blog/react-native-art-and-d3/). [Статья](https://medium.com/the-react-native-log/animated-charts-in-react-native-using-d3-and-art-21cd9ccf6c58) об использовании D3.js и ART c React Native. Альтернативные библиотеки для React: [Semiotic](https://github.com/emeeks/semiotic/) и [Recharts](http://recharts.org/en-US/).

Относится к типу **Hybrid Mobile Application** - приложение может работать медленнее, чем **Native App**.

Ограничения, свойственные конкретно Expo: 

1. В каждое приложение включается Facebook SDK, что значительно увеличивает размер **APK/IPA** (дистрибуционного комплекта)
2. Приложение Expo собирает **Advertising ID** пользователя, что нарушает Google's Developer Distribution Agreement и может быть причиной исключения из Google Play
3. Публикация приложения через Expo либо очень медленная, либо платная ($29/месяц)

Отладка кода довольно сложная и неудобная (необходимо использовать встроенный отладчик Google Chrome). Добавление Native-функций возможно в **React Native CLI** и **Expo Bare Workflow** - требует отличного знания Android Studio/Java/Kotlin и/или Xcode/Swift.

Доступен пример приложения [Guess a Number](https://github.com/Kerminator1973/GuessANumber) с инструкцией по установке инструментальных средств. Более сложный пример кода - [MealsRecipes](https://github.com/Kerminator1973/MealsRecipes).

## Типовая последовательность разработки приложения

1. Проектирование экранов и создание их прототипов
2. Разработка прототипа навигационной системы
3. Разработка класса "глобальное состояние приложения"
4. Реализация коммуникационного взаимодействия и offline-кода
5. Добавление коде аутентификации пользователя и авторизации доступа к разным функциям приложения
5. Полировка и оптимизация пользовательского интерфейса
6. Встраивание Native-функций платформы
7. Подготовка к дистрибьюции

## Ближайшее будущее

Начиная с версии 0.62 в эко-системе React Native появился новый инструмент отладки кода, под названием [Flipper](https://fbflipper.com/). Статья о средствах отладки кода для React Native, и о Flipper, в частности: [Flipper — A React Native revolution by Lukas Kurucz](https://itnext.io/flipper-a-react-native-revolution-4859d6acd685).

В конце 2020 года, React Native перейдёт на новую архитектуру - об этом статья Rémi Gallego под названием [React Native's re-architecture in 2020](https://medium.com/swlh/react-natives-re-architecture-in-2020-9bb82659792c). Ключевые улучшения:

1. Render Engine (JSI) может быть заменён на более производительный, например, на **V8**
2. Появилась возможность вызывать native С++ код из основного потока JavaScript
3. Появится гораздо больше возможностей для использоавния многопоточного кода

Выпущена новая [версия Qt 6](https://www.qt.io/blog/qt-6.0-released). Основные features:

1. Поддержка C++ 20
2. Развитие QML
3. Лучшая интеграция с Python
4. Значительно улучшенная интеграция с [CMake](https://cmake.org/).

Вместе с тем утверждается, что все LTS сборки Qt будут [только коммерческими](https://www.theregister.com/2021/01/05/qt_lts_goes_commercial_only/).

### Обновление 2024 года

Произошла фрагментация рынка мобильных операционных систем. Китайские IT-гиганты запустили: HarmonyOS (OpenHarmony) от Huawai, HyperOS (Xiaomi), ColorOS (Oppo), FuntouchOS (Vivo). В Индии разрабатывается BharOS. Российские операционные системы для мобильных устройств: Аврора ОС, Астра Мобайл, KasperskyOS, ОС Rosa Mobile, Ред ОС М (на базе Android). Поскольку многие из этих операционных систем не совместимы с Android, ключевым трендом становится **мультиплатформа**. Наиболее значимые технологии: Kotlin Multiplatform, Flutter, Qt.

Операционная система Аврора (ранее Moblin/Meego -> Sailfish) разрабатывается OOO «Открытая мобильная платформа», дочкой РосТелеКома. Базируется на Linux и содержит "Аврора TEE" - дополнительную операционную систему, реализующую функции хранения ключей, по аналогии с ARM TustZone. Пользовательский интерфейс - **Qt Framework**. Display Server - **Wayland**.

## Что не было ещё рассмотрено

Одно из наиболее интересных направлений развития направления мобильный разработки - [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html).

Я пока ещё не добрался до [Flutter](https://flutter.dev/) и [Dart](https://dart.dev/). Google **Fuchsia** - микроядерная операционная система для мобильных устройств. Основной язык разработки приложений - Dart (с Flutter). Ядро написано, преимущественно, на Rust. Dart - простой, но относительно синтаксически слабый язык программирования. Проект переведён в open-source, перспективы не очень понятны.

Большие перспективы есть у [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html), постепенно формирующий полноценную, мощную экосистему.

Фреймворк [Microsoft Xamarin](https://dotnet.microsoft.com/apps/xamarin) так же пока не был рассмотрен. Описание пользовательского интерфейса - XAML.

В 2020 году, множество компаний разрабатывают Progressive Web Apps (PWA's). Примеры: Facebook PWA, Pinterest PWA, AliExpress PWA, и т.д. PWA не нужно размещать в магазинах приложений, их легко обновлять и разрабатывать. Кажется, что у этого направления разработки - прекрасные перспективы.

Профессиональная разработка приложений под iOS возможно только на оборудовании Apple, например, на Mac Mini. Альтернативой может быть использование *Хакинтош*, но это не официальный путь, сопряжённый со множеством ограничений, включая использование старых образов ОС, потерю производительности из-за виртуализации, и т.д. Мой опыт использования Xcode на "Хакинтош", скорее негативный.

Вместе с тем, для изучения Swift и Swift UI можно использовать [Swift Playgrounds](https://apps.apple.com/ru/app/swift-playgrounds/id908519492) на iPad. Приложение содержит редактор кода и среду исполнения, в которой можно проверять прототипы приложений. Существует дополнительный инструментарий (например, платный Textastic Code Editor и Working Copy Git Client). В iPadOS 15 Swift Playground позволяет даже выполнять публикацию разработанного приложения в Apple AppStore. Существует специализированный курс на Udemy по разработке приложений непосредственно на iPad - "Create Apps on Your iPad - Swift Playgrounds for Beginners" by Nick Walter.

Пока не очень понятно, как развивает свою эко-систему [Huawei](https://developer.huawei.com/consumer/en/doc/30114).

Ссылки на [Ionic](https://ionicframework.com/) и [Apache Cordova](https://cordova.apache.org/) встречаются достаточно часто, но не настолько часто, чтобы его можно было рассматривать как конкурента React Native, или Flutter. Вместе с тем, появляется растущее число ссылок на [Iconic Capacitor](https://capacitorjs.com/), который рассматривают как развитие Iconic и Cordova.

Несомненно, очень мощный, но "не для персонального использования" - **Unity**.

# Дополнительные статьи

1. Оценка [минимальных расходов на back-end](expenses.md)
2. [Заметки](uinotes.md) о разработке пользовательского интерфейса
3. Заметки об [информационной безопасности](security.md)
