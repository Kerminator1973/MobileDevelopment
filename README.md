## Разработка мобильных приложений

Вне зависимости от используемой технологии (Android Studio/Xcode, React Native/Flutter, Qt, Xamarin), существуют общие принципы разработки мобильных приложений.

Начинать разработку приложения следует с планирования навигации, учитывающей модель информационной безопасности, хранилища состояния приложения и определения API для взаимодействия с внешними серверными компонентами.

## Навигация

Современное приложение состоит из ряда отдельных экранов (Activities), переходы между которыми позволяют получать доступ к функциям приложения. Экраны группируются некоторым способом. Под навигацией подразумеваются переходы как внутри группы, так и между группами.
Чаще всего применяются три навигационных компонента: Side Drawer, Stack и Bottom Tab (или просто Tab).

**Stack Navigator** определяет связь, в которой есть родительские и дочерние экраны. Дочерние экраны всегда располагаются поверх родительских и это обеспечивает возможность можно вернуться к родительскому экрану (например, используя кнопку «Back»).

**Tab Navigator** определяет группу экранов одного уровня между которыми можно свободно перемещаться посредством жеста swipe, либо щелчком на закладку (Tab).

**Side Drawer** определяем меню верхнего уровня, позволяя выполнить переходы на функционально не связанные, либо слабо-связанные экраны. Обычно именно Side Drawer является компонентом верхнего уровня.

Также может применяться **Switch Navigator** – особый тип экрана, который проверяет, была ли выполнена аутентификация пользователем и в зависимости от результата, перенаправляет пользователя в главное меню, либо на экран аутентификации.

## Хранилище состояния

Состояние – это любые данные, которые разделяются между различными экранами, из которых состоит приложение. Состояние может хранить *JSON Web Token* для доступа к внешним ресурсам, список валют, которыми оперирует приложение, корзина с товарами, и т.д. Данные в хранилище сохраняются при переходе от экрана к экрану.

Часто, работа с хранилищем предполагает возникновение некоторого действия пользователя, которое транслируется в слой бизнес логики, сохраняется глобально, а затем сообщение об изменении транслируется подписчиках. Например, в модели **Redux Store** действие пользователя приводит к возникновению действия (**Action**), которое может выполнить асинхронный запрос на сервер (например, с использованием [Redux-Thunk](https://github.com/reduxjs/redux-thunk)), результат действия попадает в **Reducer**-ы (при вызове функции **dispatch**()), каждый из которых может зафиксировать результат, или часть результата в свой контейнер данных, а затем подписчики (экраны) получают сообщение об изменении данных в хранилище, посредством хука **useSelector**().

Можно считать, что глобальное хранилище это и Model, и Controller в модели MVC – оно выполняет некоторую логику и хранит данные не зависимо от представления (View).

В React Native чаще всего применяется компонент [Redux Store](https://github.com/reduxjs/react-redux), но его постепенно заменяет [Context API](https://ru.reactjs.org/docs/context.html), который считается более простым для понимания и более совершенным.
 
## Пользовательский интерфейс

Для мобильных телефонов определены специальные «правила хорошего тона», к которым, например, относится *pull to update* – жест «перемещение пальца с верхней части экрана вертикально вниз» приводит к обновлению данных в списке (они повторно загружаются с сервера).

Для обеспечения удобного доступа к органам управления, большинство форм поддерживает скроллирование.

Для того, чтобы избежать перекрытия строки ввода, или другого органа управления системными элементами (чёлка, камера) используются специальные вспомогательные компоненты, такие как **SafeAreaView** в React Native.

## Доступ к Native-функциями

По мере появления новых возможностей в операционных системах, возникает необходимость доступа к этим функциями из «*not native code*». Например, для управления вибрацией и работой с биометрическим сенсором может потребоваться доступ к Native API.

В случае Qt, доступ к Android API осуществляется посредством **Java Native Interface**.

## Серверные serverless технологии

Во многих случаях, разработчикам мобильных приложений не требуется сложный backend. Может оказаться, что вполне достаточно возможности аутентификации пользователей, возможности сохранить какие-то структрурированный данные в базу (иногда достаточно пары ключ-значение), а также сохранения данных в файловое хранилище (типа S3). В подобных ситуациях разрабатывать специализированное серверное решение может оказаться изыточным.

Решением задачи может быть **serverless backend**. Наиболее типовыми решениями являются: [Google Firebase](https://firebase.google.com/), [Parse Server](https://parseplatform.org/), [AWS Lambda](https://aws.amazon.com/ru/lambda/).

Parse Server - разработка, которая была куплена Facebook, но затем "отпущена" в open-source. В [AWS](https://aws.amazon.com/marketplace/pp/Bitnami-Parse-Server-Certified-by-Bitnami/B01BLQ17TO) есть образ Parse Server от [Bitnami](https://bitnami.com/), который може6т быть развёрнут в облаке AWS за пару минут.

AWS Lambda - промежуточный уровень, описываемый как набор функций в облаке AWS, разработанных на разных языках программирования и подходит для потоковой обработки данных.

Похожая на Firebase платформа с поддержкой GraphQL- [8Base](https://www.8base.com/).
