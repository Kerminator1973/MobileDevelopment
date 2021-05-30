import React, { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, Alert, FlatList, Dimensions } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { ScreenOrientation } from 'expo';

import NumberContainer from '../components/NumberContainer';
import Card from '../components/Card';
import MainButton from '../components/MainButton';
import BodyText from '../components/BodyText';
import DefaultStyles from '../constants/default-styles';

const generateRandomBetween = (min, max, exclude) => {
    min = Math.ceil(min);
    max = Math.floor(max);
    const rndNum = Math.floor(Math.random() * (max - min)) + min;
    if (rndNum === exclude) {
        return generateRandomBetween(min, max, exclude);
    }

    return rndNum;
};

// Определяем функцию, которая будет генерировать один
// элемент списка с ранее выдвинутыми предположениями 
// угадываемого номера
const renderListItem = (listLength, itemData) => (
    <View style={styles.listItem}>
        <BodyText>#{listLength - itemData.index}</BodyText>
        <BodyText>{itemData.item}</BodyText>
    </View>
);

const GameScreen = props => {

    // Можно заблокировать поворот экрана (только в Expo), например, так:
    // ScreenOrientation.lockAsync(ScreenOrientation.OrientationLock.PORTRAIT);

    // useState - вид Hooks, который используется для
    // запуска процедуры ре-рендеринга конпонента
    const initialGuest = generateRandomBetween(1, 100, props.userChoice);
    const [currentGuess, setCurrentGuess] = useState(initialGuest);

    // Определяем ещё одно состояние, в котором мы будем хранить
    // полный список всех попыток угадывания числа для того, чтобы
    // вывести его в отдельном списке (это нужно исключительно
    // для демонстрации того, как нужно работать со списком)
    const [pastGuesses, setPastGuesses] = useState([initialGuest.toString()]);

    // useRef удобно использовать для хранения значений, которые
    // не должны изменяться при ре-рендеринге компонента. Изменение
    // свойства не приводит к ре-рендерингу
    const currentLow = useRef(1);
    const currentHigh = useRef(100);

    // Используем destructuring для того, чтобы выделить из
    // props два конкретных поля, ориентируясь на изменение которых
    // React будет понимать, нужно ли применять useEffect(), или нет
    const { userChoice, onGameOver} = props;

    //
    const [availableDeviceWidth, setAvailableDeviceWidth] = 
        useState(Dimensions.get('window').width);
    const [availableDeviceHeight, setAvailableDeviceHeight] = 
        useState(Dimensions.get('window').height);

    useEffect(() => {

        const updateLayout = () => {
            setAvailableDeviceWidth(Dimensions.get('window').width);
            setAvailableDeviceHeight(Dimensions.get('window').height);
        };

        Dimensions.addEventListener('change', updateLayout);

        // Возвращаемая из useEffect() функция называется clean up function.
        // См.: https://ru.reactjs.org/docs/hooks-reference.html#useeffect
        return () => {
            Dimensions.removeEventListener('change', updateLayout);
        };
    });

    // useEffect - особый тип hooks, который вызывается после
    // выполнения рендеринга компонента. Второй параметр - список
    // свойств, при изменении которых эффект применяется
    useEffect(() => {
        if(currentGuess === props.userChoice) {
            props.onGameOver(pastGuesses.length);
        }
    }, [currentGuess, userChoice, onGameOver]);

    const nextGuessHandler = direction => {
        if ((direction === 'lower' && currentGuess < props.userChoice) ||
            (direction === 'greater' && currentGuess > props.userChoice)) {

            Alert.alert('Don\'t lie!', 'You know that this is wrong...', 
                [{text: 'Sorry!', style: 'cancel'}]);
            return;
        }

        if (direction === 'lower') {
            currentHigh.current = currentGuess;
        } else {
            currentLow.current = currentGuess + 1;
        }

        const nextNumber = generateRandomBetween(currentLow.current, 
            currentHigh.current, currentGuess);
        setCurrentGuess(nextNumber);

        // Добавляем в список сделанных предположений ещё одно,
        // для того, чтобы отобразаить его в соответствующем списке
        setPastGuesses(curPastGuesses => [nextNumber.toString(),...curPastGuesses]);
    };

    if (availableDeviceHeight < 500) {

        return (         
            <View style={styles.screen}>
            <Text style={DefaultStyles.title}>Oppenent's Guess</Text>
            <View style={styles.control}>
                <MainButton onPress={nextGuessHandler.bind(this, 'lower')}>
                    <Ionicons name="md-remove" size={24} color="white" />
                </MainButton>
                <NumberContainer>{currentGuess}</NumberContainer>

                <MainButton onPress={nextGuessHandler.bind(this, 'greater')}>
                    <Ionicons name="md-add" size={24} color="white" />
                </MainButton>
            </View>

            <View style={styles.listContainer}>
                <FlatList keyExtractor={(item) => item}
                    data={pastGuesses} 
                    renderItem={
                        renderListItem.bind(this, pastGuesses.length)
                    } 
                    contentContainerStyle={styles.list}
                />
            </View>
        </View>);
    }

    return (
        <View style={styles.screen}>
            <Text style={DefaultStyles.title}>Oppenent's Guess</Text>
            <NumberContainer>{currentGuess}</NumberContainer>
            <Card style={styles.buttonContainer}>
                <MainButton onPress={nextGuessHandler.bind(this, 'lower')}>
                    <Ionicons name="md-remove" size={24} color="white" />
                </MainButton>
                <MainButton onPress={nextGuessHandler.bind(this, 'greater')}>
                    <Ionicons name="md-add" size={24} color="white" />
                </MainButton>
            </Card>
            <View style={styles.listContainer}>
                {/*<ScrollView contentContainerStyle={styles.list}>
                    {pastGuesses.map((guess, index) => 
                        renderListItem(guess, pastGuesses.length - index))}
                    </ScrollView>*/}
                <FlatList keyExtractor={(item) => item}
                    data={pastGuesses} 
                    renderItem={
                        renderListItem.bind(this, pastGuesses.length)
                    } 
                    contentContainerStyle={styles.list}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    screen: {
        flex: 1,
        padding: 10,
        alignItems: 'center'
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginTop: Dimensions.get('window').height > 600 ? 30 : 5,
        width: 400,
        maxWidth: '90%'
    },
    control: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: '80%',
        alignItems: 'center'
    },
    listContainer: {
        flex: 1,
        width: Dimensions.get('window').width > 350 ? '60%' : '80%'
    },
    list: {
        flexGrow: 1,
        // alignItems: 'center',
        justifyContent: 'flex-end' // Элементы добавляются снизу вверх
    },
    listItem: {
        borderColor: '#ccc',
        padding: 15,
        marginVertical: 10,
        backgroundColor: 'white',
        borderWidth: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%'
    }
});

export default GameScreen;
