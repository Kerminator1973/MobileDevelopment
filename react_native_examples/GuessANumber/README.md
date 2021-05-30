# Guess A Number
Приложение "Угадай число" демонстрирует как повторно использовать стили в приложениях React Native, а так же общий подход организации приложения с большим количеством форм взаимодействия с пользователем. Приложение использует [Expo](https://expo.io/) в качестве среды запуска.

В качестве package manager-а рекомендуется использовать [yarn](https://classic.yarnpkg.com/en/), поскольку он более быстрый (2X), более надёжный, чем **npm**.

Для установки Expo CLI следует выполнить команду: `yarn global add expo-cli`

Загрузка зависимостей проекта: `yarn install`

Запуск приложения: `yarn start`

## Как создавался проект

В Expo существует генератор начального кода, который вызывается командой: `expo init my-new-project`.

## Boilerplate code

При создании новых компонентов, рекомендуется использовать следующие boilerplate code:

```
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
const NewComponent = props => {
    return (
        <View></View>
    );
};
const styles = StyleSheet.create({});
export default NewComponent;
```

## Часто встречающиеся синтаксические конструкции

### Spread operator, или Rest operator

В приложениях React/React Native повсеместно используется **spread-оператор**, который используется для объединения двух объектов в один. Пример использования:

```javascript
const Input = props => {
    return <TextInput {...props} style={{...styles.input, ...props.style }}/>
};    
```

В приведённом выше примере указывается, что в аттрибуте **style** необходимо объединить все элементы объектов (контейнеров) styles.input и props.style. Практический смысл - в styles.input хранятся стили, общие для всех экземпляров, а через props.style устанавливаются стили, уникальные для конкретного экземпляра компонента.

### Object destructuring

**Destructuring** - способ извлечения данных сохранённых в объектах, или массивах. Пример использования:

```javascript
var instructor = {
               firstName: "Elie",
               lastName: "Schoppik"
}
var {firstName, lastName} = instructor;
firstName;          // "Elie"
lastName;           // "Schoppik"
```

Более *advanced* пример использования:

```javascript
const product = {
	label: 'Red notebook',
	price: 3,
	stock: 201,
	salePrice: undefined
}
... 
const {label, stock} = product
```

Ключевым в синтаксисе является использование фигурных скобок. Имена переменных в фигурных скобках должны точно соответствовать именам полей в объекте, чтобы ES6 понял, какие именно поля объекта нужно скопировать в локальные переменные.

Чтобы создать локальную переменную с другим именем, следует явно указать *mapping*:

```javascript
const {label:productLabel, stock} = product
console.log(productLabel)		// 'Red notebook'
```

Mapping указывается посредством символа двоеточия, сначала идёт имя поля из объекта (label), а затем имя создаваемой локальной переменной (productLabel).

Также мы можем использовать «значение по умолчанию» на случай, если указанного поля в объекте нет:

```javascript
const {label:productLabel, stock, rating = 5} = product
```

Ещё более *advanced* способ использования object destructuring – включить его непосредственно в определение функции, например:

```javascript
const transaction = (type, { label, stock }) => {
	console.log(type, label, stock)
}
...
transaction('order', product)
```

ES6 понимает, что второй параметр функции transaction() является объектом, из которого нужно извлечь поля label и stock и использовать их значения для создания локальных переменных label и stock.

Чтобы защитить код в случаях, если объект-параметр не будет указан, рекомендуется использовать "значение по умолчанию":

```javascript
geocode(address, (error, {latitude, longitude, location} = {} ) => {
```

В этом случае, если при вызове callback-функции не будет указан второй параметр (по правилам JavaScript он будет установлен в undefined), то ему будет присвоено пустое множество, что позволит избежать сбрасывания исключения. В JavaScript мы не можем применить операцию **object destructuring** для значения **undefined**.

Более простой пример:

```javascript
const greet = (name = 'User') => {
	console.log('Hello, ' + name + '!')
}
greet('Andrew')		// Напечатает: Hello, Andrew!
greet()			// Напечатает: Hello, User!
```

## Повторное использование стилей

Для повторного использования стилей в React Native используются специальные wrapper-компоненты, которые не содержат никакой логики, но генерируют View, содержащий как повторно используемый набор стилей, так и настраиваемые для конкретного случая стили.
Пример wrapper-компонента со стилями:

```
import React from 'react';
import { View, StyleSheet } from 'react-native';
const Card = props => {
    return <View style={{...styles.card, ...props.style}}>{props.children}</View>
};
const styles = StyleSheet.create({
    card: {
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 2 },
        shadowRadius: 6,
        shadowOpacity: 0.26,
        elevation: 5,
        backgroundColor: 'white',
        padding: 20,
        borderRadius: 10        
    }
});
export default Card;
```

Поле **{props.children}** означает «включить сюда все дочерние компоненты».

Более каноничная форма определения компонента - после View следует конструкция `{...props}`, которая указывает, что нужно скопировать все атрибуты применённые к конкретному экземпляру Card и к View.:

```
const Card = props => {
    return <View {...props} style={{...styles.card, ...props.style}}>{props.children}</View>
};
```

Использование этого wrapper-компонента может выглядеть так:
```
import Card from '../components/Card';

const StartGameScreen = props => {
    return (
        <View style={styles.screen}>
            <Text style={styles.title}>Start a New Game!</Text>
            <Card style={styles.inputContainer}>
                <Text>Select a Number</Text>
                <TextInput />
                <View style={styles.buttonContainer}>
                    <Button title="Reset" onPress={() => {}}/>
                    <Button title="Confirm" onPress={() => {}} />
                </View>
            </Card>
        </View>
    );
};
```

Дополнительное замечание: для Android и iOS могут использоваться разные наборы стилей. Так, например, стили: shadowColor, shadowOffset, shadowOpacity и shadowRadius работают только на iOS, а в Android следует использовать свойство **elevation** со значением 5, что означает – *использовать стиль Material Design*. Соответственно, оба наборов стилей нужно использовать в StyleSheet для обеспечения совместимости с обоими платформами.

## Использование экранов (Screens)

Приложение на React Native имеет признаки приложения с использованием конечного автомата. Состояние конечного автомата - это состояние root-компонента приложения React Native. При переходе из одного состояния в другое скрываются все "экраны", размещённые в root-компоненте за исключением одного, соответствующего текущему состоянию конечного автомата.

Подобный подход почти полностью соответствует Qt 5 с QML, приложение на котором также изначально следует проектировать основываясь на состояниях конечного автомата. Отчасти схожий подход используется и в приложениях на Java/Kotlin, основывающихся на Android SDK, но в них переходы из Activity в Activity могут носить более сложный характер, а часть Activity могут являться "черными ящиками".

## Различные типы hooks

**Rendering cycle** - время между двумя операциями по созданию разметки в React-компоненте. Rendering выполняется каждый раз, когда изменяется состояние компонента.

Существует возможность сохранить некоторые данные компонента при повторном rendering-е. Для этого используется hook **useRef**. Этот тип hook-ов отвечает только за хранение значений вне зависимости от rendering cycles. Изменение useRef не приводит к повторному rendering-у и по этой причине, они чаще всего относятся к бизнес-логике приложения. Пример использования:

```javascript
import React, { useRef } from 'react';
...
const GameScreen = props => {
...
    const currentLow = useRef(1);
    const currentHigh = useRef(100);
    
    const nextGuessHandler = direction => {
        if (direction === 'lower') {
            currentHigh.current = currentGuess;
        } else {
            currentLow.current = currentGuess;
        }
    }
```

Hooks типа **useState** используются для выполнения повторного rendering-а конкретного компонента. Когда вызывается функция, созданная вызовом useState(), React Native ставит задачу rendering-а компонента в очередь. Пример использования:

```javascript
import React, { useState, useRef } from 'react';
...
const StartGameScreen = props => {
    const [enteredValue, setEnteredValue] = useState('');
...
    const numberInputHandler = inputText => {
        setEnteredValue(inputText.replace(/[^0-9]/g, ''));
    };
 ...
    return (
        <TouchableWithoutFeedback onPress={() => {
            Keyboard.dismiss();
        }}>
        <View style={styles.screen}> ...
                <Input style={styles.input} 
                    blurOnSubmit 
                    autoCapitalize='none' 
                    autoCorrent={false} 
                    keyboardType="number-pad" 
                    maxLength={2} 
                    onChangeText={numberInputHandler}
                    value={enteredValue}
                /> 
```

## Некоторые замечания по сопровождению кода

Перед запуском приложения на эмуляторе Android-устройства, следует предварительно запустить эмулятор. В эмуляторе Android-устройства должно быть установлено приложение Expo.

Обновление зависимостей можно выполнять используя вспомогательный инструментарий, например:

```
npm install -g npm-check-updates
ncu -u
npm install
```

Просто посмотреть зависимости можно командой `ncu` без параметров.
