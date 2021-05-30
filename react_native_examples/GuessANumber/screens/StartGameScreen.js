import React, { useState, useEffect } from 'react';
import { 
    View, 
    Text, 
    StyleSheet, 
    Button,
    TouchableWithoutFeedback,
    Keyboard,
    Alert,
    Dimensions,
    ScrollView,
    KeyboardAvoidingView
} from 'react-native';

import Input from '../components/Input';
import Card from '../components/Card';
import Colors from '../constants/colors';
import BodyText from '../components/BodyText';
import TitleText from '../components/TitleText';
import NumberContainer from '../components/NumberContainer';
import MainButton from '../components/MainButton';

const StartGameScreen = props => {

    // Добавляем состояние дя того, чтобы иметь возможность
    // добавить валидацию пользовательского ввода
    const [enteredValue, setEnteredValue] = useState('');

    // Добавляем состояние "подтверждение выбора"
    const [confirmed, setConfirmed] = useState(false);
    
    // Добавляем ещё одно состояние - введённое пользователем
    // числовое значение
    const [selectedNumber, setSelectedNumber] = useState(0);

    // Состояние будет использоваться для перерисовки экрана
    // при изменении Dimensions, в частности, при повороте экрана
    const [buttonWidth, setButtonWidth] = 
        useState(Dimensions.get('window').width / 4);

    const numberInputHandler = inputText => {
        // При обработке изменений в строке редактирования
        // применяем регулярное выражение, в котором меняем всё,
        // что не входит в диапазон от 0 от 9 на пустой символ,
        // т.е. блокируем возможность ввода чего-либо 
        // отличного от цифр
        setEnteredValue(inputText.replace(/[^0-9]/g, ''));
    };

    const resetInputHandler = () => {
        setEnteredValue('');
        setConfirmed(false);
    };

    // Подключаем EventListener, который будет обновлять необходимый
    // нам параметр при изменении Dimensions, например, при повороте
    // экрана. Вызов removeEventListener() нужен для того, чтобы
    // избежать многократного дублирования подписывания на события
    useEffect(() => {

        const updateLayout = () => {
            setButtonWidth(Dimensions.get('window').width / 4);
        }
    
        Dimensions.addEventListener('change', updateLayout);

        // Возвращаемая из useEffect() функция называется clean up function.
        // См.: https://ru.reactjs.org/docs/hooks-reference.html#useeffect
        return () => {
            Dimensions.removeEventListener('change', updateLayout);
        };
    });

    const confirmInputHandler = () => {

        const chosenNumber = parseInt(enteredValue);
        if (isNaN(chosenNumber) || chosenNumber <= 0 || chosenNumber > 99) {

            // Выводим информационное сообщение об ошибке
            Alert.alert('Invalid number!', 
                'Number has to be a number between 1 and 99.', 
                [{text: 'Ok', style: 'destuctive', onPress: resetInputHandler}]);
            return;
        }

        setConfirmed(true);
        setEnteredValue('');

        // Мы можем использовать значение enteredValue, т.к.
        // изменение этой переменной, связанной с вызовом
        // setEnteredValue('') выше по коду, будут применены
        // только в следующем render cycle
        setSelectedNumber(chosenNumber);

        Keyboard.dismiss();
    };

    // Этот код будет выполняться при каждом rendering cycle,
    // т.е. каждый раз, когда какое-либо из состояний данной формы
    // будет изменено.
    // Мы можем динамически формировать некоторый контент и включать
    // его в JSX. Это, в частности, позволяет выполнять проверку/отладку
    // кода более эффективно
    let confirmedOutput;
    if (confirmed) {
        confirmedOutput = 
            <Card style={styles.summaryContainer}>
                <Text>You selected</Text>
                <NumberContainer>{selectedNumber}</NumberContainer>
                <MainButton onPress={() => props.onStartGame(selectedNumber)}>
                    START GAME
                </MainButton>
            </Card>
    }

    return (
        <ScrollView>
        <KeyboardAvoidingView behavior="position" keyboardVerticalOffset={30}>
        <TouchableWithoutFeedback onPress={() => {
            Keyboard.dismiss();
        }}>
        <View style={styles.screen}>
            <TitleText style={styles.title}>Start a New Game!</TitleText>
            <Card style={styles.inputContainer}>
                <BodyText>Select a Number</BodyText>
                <Input style={styles.input} 
                    blurOnSubmit 
                    autoCapitalize='none' 
                    autoCorrent={false} 
                    keyboardType="number-pad" 
                    maxLength={2} 
                    onChangeText={numberInputHandler}
                    value={enteredValue}
                />
                <View style={styles.buttonContainer}>
                    {/* Используем inline styles, чтобы динамически 
                        пересчитывать ширину кнопок (buttonWidth) */}
                    <View style={{width: buttonWidth}}>
                        <Button title="Reset" onPress={resetInputHandler} 
                            color={Colors.accent}/>
                    </View>
                    <View style={{width: buttonWidth}}>
                        <Button title="Confirm" onPress={confirmInputHandler} 
                            color={Colors.primary}/>
                    </View>
                </View>
            </Card>
            {confirmedOutput}
        </View>
        </TouchableWithoutFeedback>
        </KeyboardAvoidingView>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    screen: {
        flex: 1,
        padding: 10,
        alignItems: 'center'
    },
    title: {
        fontSize: 20,
        marginVertical: 10
    },
    inputContainer: {
        width: '80%',
        maxWidth: '95%',    // Предотвращаем выход за пределы экрана
        minWidth: 300,      // Настройка для устройств с маленьким разрешением
        alignItems: 'center',
    },
    buttonContainer: {
        flexDirection: 'row',
        width: '100%',
        justifyContent: 'space-between',
        paddingHorizontal: 15
    },
    // button: {
    //      width: 100
    //      width: Dimensions.get('window').width / 4
    // },
    input: {
        width: 50,
        textAlign: 'center',
    },
    summaryContainer: {
        marginTop: 20,
        alignItems: 'center',
    }
});

export default StartGameScreen;
