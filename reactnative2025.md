# UNDER CONSTRUCTION! Восстановление навыков разработки на React Native в 2025 году

Подготовка стенда предполагает установленные и сконфигурированные Node.js, JDK и Android Studio.

Т.к. на моей машине была установлена старая версия react-native-cli, при генерации приложения из шаблона, возникала критичная ошибка. Мне пришлось удалить старые инструментальные средства, выполнив следующую команду с от имени администратора системы:

```shell
npm uninstall -g react-native-cli
```

Рекомендуется почистить npm cache:

```shell
# Clear npm cache
npm cache clean --force

# Clear React Native CLI cache
npx react-native clean
```

Установил обновлённые компоненты:

```shell
npm install -g @react-native-community/cli
npm install -g react-native
```

Сгенерировать шаблон приложения можно следующей командой:

```shell
npx @react-native-community/cli init CountReader --template react-native-template-typescript@latest
```

Запуск приложения на эмуляторе Android:

```shell
npx react-native run-android
```

Для проверки корректности настроек проекта можно использовать команду **doctor**:

```shell
npx react-native doctor
```

Приложение собралось и запустилось на эмуляторе. Проект содержит полноценный проект для Android Studio. 

Код на React Native состоит из единственного файла "App.tsx".

Запустив базовое приложении в эмуляторе можно дважды нажать кнопку R, чтобы перезапустить код приложения. Комбинация `Ctrl+M`, или встряска мобильного телефона позволяет открыть откладочное меню приложения.

Информация при запуске приложения: "info React Native v0.81.0 is now available (your project is running on v0.70.10)."

## Активировать "Режим разработчика" в Android

Для активации режима разработчика в Android-приложении необходимо запустить приложение "Settings", перейта в раздел "About device" и найти в этом разделе информацию о номере сборки (Build). На телефоне realme 9 Pro, потребовалось войти в раздел "Version" (не "Android version") и найти строку "Version number". Затем необходимо 7 раз нажать на строку "Version number" (или "Build" - зависит от телефона). После этого Android потребует ввести ПИН-код для подтверждения действия и после подтверждения телефон будет переведён в режим разработчика.

Следующим шагом необходимо разрешить отладку по USB. Для этого в "Settings -> Additional Settings" нужно найти раздел "Developer Options" и разрешить "USB Debugging".

После разрешения отладки по USB, в "Device Manager" Android Studio появится новое физическое устройство на котором можно запускать разрабатываемое приложение, или вести его отладку.

Также можно выполнить Pairing устройств по Wi-Fi и осуществлять беспроводную отладку кода.
