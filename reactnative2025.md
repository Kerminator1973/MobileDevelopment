# Восстановление навыков разработки на React Native в 2025 году

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

Если необходимо внести какие-то существенные изменения в проект, то приложение можно остановить через консоль, в которой исполняется Bundler Metro, а после внесения изменений сново запустить в этой же консоли посредством команды `npx react-native run-android`. Останавливать эмулятор QEMU с Android не нужно.

Информация при запуске приложения: "info React Native v0.81.0 is now available (your project is running on v0.70.10)."

## Активировать "Режим разработчика" в Android

Для активации режима разработчика в Android-приложении необходимо запустить приложение "Settings", перейта в раздел "About device" и найти в этом разделе информацию о номере сборки (Build). На телефоне realme 9 Pro, потребовалось войти в раздел "Version" (не "Android version") и найти строку "Version number". Затем необходимо 7 раз нажать на строку "Version number" (или "Build" - зависит от телефона). После этого Android потребует ввести ПИН-код для подтверждения действия и после подтверждения телефон будет переведён в режим разработчика.

Следующим шагом необходимо разрешить отладку по USB. Для этого в "Settings -> Additional Settings" нужно найти раздел "Developer Options" и разрешить "USB Debugging".

После разрешения отладки по USB, в "Device Manager" Android Studio появится новое физическое устройство на котором можно запускать разрабатываемое приложение, или вести его отладку.

Также можно выполнить Pairing устройств по Wi-Fi и осуществлять беспроводную отладку кода.

## Как запустить приложение React Native на мобильном телефоне

К сожалению, с загрузкой проекта в Android Studio могут возникнуть проблемы - если указать папку "Android" в среда разработки попытается загрузить проект, но, с высокой вероятностью, в структуре проекта существует несовместимости со средой разработки Android и IDE не сможет адаптировать настройки автоматически.

Собрать приложение в DEBUG-режиме можно запустив команду сборки из папки "Android":

```shell
gradlew assembleDebug
```

Release-сборку можно собрать используя скрипт:

```shell
gradlew assembleRelease
```

Результат сборки - apk-файл, которые может быть размещён:

- Debug APK: android/app/build/outputs/apk/debug/app-debug.apk
- Release APK: android/app/build/outputs/apk/release/app-release.apk

Собранный apk-файл необходимо скопировать в файловую систему телефона, в "Setting" необходимо разрешить установку приложений из неизвестных источников. После этого можно запустить процедуру установки приложения на телефоне.

Приложение устанавливается и прекрасно работает.

## Upgrade приложения

Сообщество разработчиков предлагает использовать вспомогательную [online-утилиту](https://react-native-community.github.io/upgrade-helper/)

UNDER CONSTRUCTION: как переключить проект на использование React Native v0.81.0, а не v0.70.10.

## UNDER CONSTRUCTION: считывание QR-кода посредством камеры

Поддержать функционал не удалось. Были добавлены следующие зависимости:

```shell
npm install react-native-vision-camera
npm install react-native-reanimated
npm install vision-camera-code-scanner
```

В файле манифеста "AndroidManifest.xml" было добавлено разрешение использования камеры:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

После этого, при попытке сборке проекта выводится следующее сообщение:

```output
BUILD FAILED in 2s

    at makeError (d:\Sources\Playground\CountReader\node_modules\@react-native-community\cli-platform-android\node_modules\execa\index.js:174:9)
    at d:\Sources\Playground\CountReader\node_modules\@react-native-community\cli-platform-android\node_modules\execa\index.js:278:16
    at process.processTicksAndRejections (node:internal/process/task_queues:105:5)
    at async runOnAllDevices (d:\Sources\Playground\CountReader\node_modules\@react-native-community\cli-platform-android\build\commands\runAndroid\runOnAllDevices.js:109:5)
    at async Command.handleAction (d:\Sources\Playground\CountReader\node_modules\@react-native-community\cli\build\index.js:142:9)
```

Очень похоже на то, что существуют проблемы, связанные с зависимостями компонентов, что требует значимых усилий по их решению.

## Создание приложения с использованием Expo

[Expo](https://expo.dev/) - альтернативный подход создания приложений React Native. В обычном подходе создаётся отдельное Android-приложение, в котором есть нативный код (C++), код JVM и JavaScript-код React Native-приложения. Вся эта совокупность кода компилируется в apk-файл и с точки зрения операционной системы - это обычное Android-приложение. В случае использования Expo, нативный код, и код JVM используется в Android-приложении Expo Go, которое можно загрузить из Google Market. Это приложение содержит необходимые разрешения, уровни взаимодействия с "железом" и функционал загрузки внешних приложений. В парадигме Expo, React Native-приложение это только bundled JavaScript-код, подготовленный посредством [Metro Bundler](https://metrobundler.dev/). Скомпанованный Bundle доставляется на мобильные телефоны через сайт Expo, или по локальной сети через запущенный в Metro web-сервер и запускается в песочнице приложения Expo Go.

>На сайте Expo указывается, что Expo Go - это только Preview приложения, а полноценное приложение компилируется посредством сервиса Expo Application Services (EAS) Submit и, возможно, эта публикация осуществляется на Play Market, а получается полноценный apk.
>
>В результате проверки оказалось, что без подключения к Metro Bundler приложение не работает, т.е. Expo Go это действительно только клиент для отладки кода. Полноценный apk для распространения через Google Play можно получить через CI/CD от Expo.

Создать приложение из шаблона можно командой:

```shell
npx create-expo-app CountScanner
```

Структура проекта гораздо больше похожа на типовые приложения на React.

Запустить проект можно командой:

```shell
npx expo start
```

Expo использует более свежую версию React Native 0.79.5.

Загрузка приложения на мобильный телефон осуществляется через сетевое соединение. [Metro Bundler](https://metrobundler.dev/) создаёт web-сервер на порту компьютера разработчика и выводит в консоль QR-код с IP-адресом и портом этого сервера в локальной сети. Мобильное приложение Expo Go, считывает QR-код с IP-адресом сервера, подключается к нему, скачивает Bundle и запускает React Native-приложение в своей среде. 

IP-адрес сервера можно ввести вручную. Публиковать приложения можно через сайт [Expo](https://expo.dev/). Бесплатный тарифный план предлагает загрузку не более 30 приложений и рассылает обновления не более, чем 1000 пользователям в месяц.

Для добавления функционала считывания QR-кода следует добавить в проект следующие зависимости:

```shell
npx expo install expo-camera expo-barcode-scanner
```

DeepSeek предлагает вполне работающий вариант считывания QR-кода, который является главным компонентом приложения. В простом шаблоне приложения, нам нужно найти файл "App.js", в случае, если был выбран более сложный шаблон, может потребоваться найти файл "\app\_layout.tsx" и сохранить в нём следующий код:

```jsx
import React, { useState, useEffect } from 'react';
import { Text, View, StyleSheet, Button, Linking } from 'react-native';
import { CameraView, Camera } from 'expo-camera';
import * as BarcodeScanner from 'expo-barcode-scanner';

export default function App() {
  const [hasPermission, setHasPermission] = useState(null);
  const [scanned, setScanned] = useState(false);
  const [data, setData] = useState('No QR code scanned yet');
  const [activeScanner, setActiveScanner] = useState(false);

  useEffect(() => {
    const getCameraPermissions = async () => {
      const { status } = await Camera.requestCameraPermissionsAsync();
      setHasPermission(status === 'granted');
    };

    getCameraPermissions();
  }, []);

  const handleBarCodeScanned = ({ type, data }) => {
    setScanned(true);
    setData(data);
    alert(`QR Code with data ${data} has been scanned!`);
  };

  const openLink = () => {
    if (data.startsWith('http://') || data.startsWith('https://')) {
      Linking.openURL(data);
    } else {
      alert('Scanned data is not a valid URL');
    }
  };

  if (hasPermission === null) {
    return <Text>Requesting for camera permission</Text>;
  }
  if (hasPermission === false) {
    return <Text>No access to camera</Text>;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>QR Code Scanner</Text>
      
      {activeScanner ? (
        <View style={styles.cameraContainer}>
          <CameraView
            onBarcodeScanned={scanned ? undefined : handleBarCodeScanned}
            barcodeScannerSettings={{
              barcodeTypes: ["qr", "pdf417"],
            }}
            style={styles.camera}
          />
          {scanned && (
            <Button 
              title={'Tap to Scan Again'} 
              onPress={() => {
                setScanned(false);
                setData('No QR code scanned yet');
              }} 
            />
          )}
        </View>
      ) : (
        <View style={styles.placeholder}>
          <Text style={styles.placeholderText}>Camera not active</Text>
        </View>
      )}
      
      <View style={styles.controls}>
        <Button
          title={activeScanner ? "Stop Scanner" : "Start Scanner"}
          onPress={() => {
            setActiveScanner(!activeScanner);
            setScanned(false);
          }}
        />
      </View>
      
      <View style={styles.resultContainer}>
        <Text style={styles.resultTitle}>Scanned Data:</Text>
        <Text style={styles.resultData}>{data}</Text>
        {data.startsWith('http') && (
          <Button title="Open Link" onPress={openLink} />
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
    backgroundColor: '#ecf0f1',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
  },
  cameraContainer: {
    flex: 5,
    flexDirection: 'column',
    justifyContent: 'flex-end',
    marginBottom: 20,
    borderRadius: 10,
    overflow: 'hidden',
  },
  camera: {
    flex: 1,
  },
  placeholder: {
    flex: 5,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#d1d1d1',
    marginBottom: 20,
    borderRadius: 10,
  },
  placeholderText: {
    fontSize: 16,
    color: '#666',
  },
  controls: {
    marginBottom: 20,
  },
  resultContainer: {
    padding: 15,
    backgroundColor: 'white',
    borderRadius: 10,
    marginBottom: 20,
  },
  resultTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  resultData: {
    fontSize: 16,
    marginBottom: 10,
    color: '#333',
  },
});
```

Приложение является работоспособным, считывает QR-коды в кодировках QR и PDF417.

Metro Bundler позволяет запускать React DevTools, можно смотреть логи, есть доступ к профилировщику, но пока не очень понятно, как можно было бы вести отладку кода, непосредственно в Visual Studio Code.

## Использование EAS

Использование сервиса требует регистрации пользователя в системе.

Также необходимо установить консольные утилиты (от имени администратора):

```shell
npm install --global eas-cli
```

При создании проекта, EAS создает GUID проекта, который нужно применить к папке с проектом. Например:

```shell
eas init --id fa93fed2-5208-4c43-bff6-a4c0ba473701
```

Команда связывает папку на диске с проектом в EAS.

Команда сборки приложения в консоли выглядит следующим образом:

```shell
eas build:configure
```

Сборка на сервере запускается следующим образом:

```shell
eas build
```

Перед началом сборки требуется выбрать платформу, сгенерировать ключи, определить slug, дать имя сборке (в терминах Android). Далее сборка будет запланирована на сервере и, в какой-то момент, будет запущена.

Однако, в моём случае, сборка не была успешной:

```output
× Build failed

🤖 Android build failed:
Gradle build failed with unknown error. See logs for the "Run gradlew" (https://expo.dev/accounts/kerminator1973/projects/countscanner/builds/592d78b3-89ca-4dbf-9ce6-05cecf7473ee#run-gradlew) phase for more information.
```

По результатам анализа сбоя, была запущена команда:

```shell
npx expo install --check
```

Команда обновила версию React Native.

Однако основная проблема сборки была связана с использованием компонента expo-barcode-scanner:

```output
* What went wrong:
Execution failed for task ':expo-barcode-scanner:compileReleaseKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details
```

Поиск проблемы в интернет дал следующий совет:

```output
If you use Expo SDK 48/49 etc., check Expo docs for the recommended kotlinVersion/AGP/Gradle versions. Mismatches commonly cause the Kotlin compiler runner failure.
```

На сайте проекта [Expo BarCodeScanner](https://docs.expo.dev/versions/v51.0.0/sdk/bar-code-scanner/) указывается, что "_Deprecated: This library will no longer be available from SDK 51. We recommend using expo-camera which has barcode scanning built-in instead._"

Также в документации написано, что можно сконфигурировать права, предоставляемые приложению:

```json
{
  "expo": {
    "plugins": [
      [
        "expo-camera",
        {
          "cameraPermission": "Allow $(PRODUCT_NAME) to access your camera",
          "microphonePermission": "Allow $(PRODUCT_NAME) to access your microphone",
          "recordAudioAndroid": true
        }
      ]
    ]
  }
}
```

В результате я взял пример работы с камерой из официальной документации библиотеки [expo-camera](https://docs.expo.dev/versions/v51.0.0/sdk/camera/#usage) и доработал его добавив функционал считывания QR-кода:

```js
import { CameraType, CameraView, useCameraPermissions } from 'expo-camera';
import { useState } from 'react';
import { Button, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export default function App() {
  const [facing, setFacing] = useState<CameraType>('back');
  const [permission, requestPermission] = useCameraPermissions();
  const [scanned, setScanned] = useState(false);

  if (!permission) {
    // Camera permissions are still loading.
    return <View />;
  }

  if (!permission.granted) {
    // Camera permissions are not granted yet.
    return (
      <View style={styles.container}>
        <Text style={styles.message}>We need your permission to show the camera</Text>
        <Button onPress={requestPermission} title="grant permission" />
      </View>
    );
  }

  function toggleCameraFacing() {
    setFacing(current => (current === 'back' ? 'front' : 'back'));
  }

  const handleBarCodeScanned = ({ data, type }) => {
    setScanned(true);
    alert(`QR code scanned: ${data}`);
  };

  return (
    <View style={styles.container}>
      <CameraView 
        style={styles.camera} 
        facing={facing} 
        onBarcodeScanned={scanned ? undefined : handleBarCodeScanned}
        barcodeScannerSettings={{
          barcodeTypes: ["qr"],
        }}>
        <View style={styles.buttonContainer}>
          <TouchableOpacity style={styles.button} onPress={toggleCameraFacing}>
            <Text style={styles.text}>Flip Camera</Text>
          </TouchableOpacity>
        </View>
      </CameraView>
      {scanned && (
        <Button title={'Tap to Scan Again'} onPress={() => setScanned(false)} />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  message: {
    textAlign: 'center',
    paddingBottom: 10,
  },
  camera: {
    flex: 1,
  },
  buttonContainer: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: 'transparent',
    margin: 64,
  },
  button: {
    flex: 1,
    alignSelf: 'flex-end',
    alignItems: 'center',
  },
  text: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
  },
});
```

Установить библиотеку можно следующим образом:

```shell
npx expo install expo-camera
```
