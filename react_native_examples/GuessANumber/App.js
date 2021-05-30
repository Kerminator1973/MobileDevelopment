import React, { useState } from 'react';
import { StyleSheet, View } from 'react-native';
import * as Font from 'expo-font';
import { AppLoading } from 'expo';

import Header from './components/Header';
import StartGameScreen from './screens/StartGameScreen';
import GameScreen from './screens/GameScreen';
import GameOverScreen from './screens/GameOverScreen';

// Определяем функцию, которая умеет загружать шрифты из assets
// React Native. Эта функция возвращает Promise, т.е. является
// асинхронной. Функцию следует использовать во всех компонентах, 
// у которых есть потребность в соответствующих шрифтах
const fetchFonts = () => {
  return Font.loadAsync({
    'open-sans': require('./assets/fonts/OpenSans-Regular.ttf'),
    'open-sans-bold': require('./assets/fonts/OpenSans-Bold.ttf')
  });
};

export default function App() {

  const [userNumber, setUserNumber] = useState();
  const [guessRounds, setGuessRounds] = useState(0);

  // Hook используется для перерисовки контента после того, как
  // шрифты будут загружены
  const [dataLoaded, setDataLoaded] = useState(false);

  // При первом запуске будет выведен компонет <AppLoading />
  // который запустит процесс загрузки шрифтов. После того, как
  // шрифты будут загружены, осуществиться повторный рендеринг
  // компонента дальше по коду
  if (!dataLoaded) {
    return <AppLoading startAsync={fetchFonts} 
      onFinish={() => setDataLoaded(true)}
      onError={(err) => console.log(err)} />;
  }

  // Этот callback будет вызван в случае, если игра должна
  // быть перезапущена
  const configureNewGameHandler = () => {
    setGuessRounds(0);
    setUserNumber(null);
  };

  // Функция, посредством котором можно влиять на то, какой из экранов
  // будет отображаться в данный момент
  const startGameHandler = (selectedNumber) => {
    setUserNumber(selectedNumber);
    setGuessRounds(0);
  };

  // Callback-функция вызывается когда компьютер угадал число 
  const gameOverHandler = numOfRounds => {
    setGuessRounds(numOfRounds);
  };

  let content = <StartGameScreen onStartGame={startGameHandler} />;
  if (userNumber && guessRounds <= 0) {
    content = <GameScreen userChoice={userNumber} onGameOver={gameOverHandler}/>;
  } else if (guessRounds > 0) {
    content = <GameOverScreen roundsNumber={guessRounds} 
      userNumber={userNumber} onRestart={configureNewGameHandler} />;

  }

  return (
    <View style={styles.screen}>
      <Header title="Guess a number" />
      {content}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1
  }
});
