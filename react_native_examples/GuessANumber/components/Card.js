import React from 'react';
import { View, StyleSheet } from 'react-native';

const Card = props => {
    // Используем spread-operator для того, чтобы иметь возможность
    // добавить некоторые дополнительные стили к стилям, которые
    // мы используем в данном wrapper-компоненте
    return <View style={{...styles.card, ...props.style}}>{props.children}</View>
};

const styles = StyleSheet.create({
    card: {
        shadowColor: 'black',   // shadow - свойства используемые только в iOS
        shadowOffset: { width: 0, height: 2 },
        shadowRadius: 6,
        shadowOpacity: 0.26,
        elevation: 5,           // свойство Android, означает "use Material Design"
        backgroundColor: 'white',
        padding: 20,
        borderRadius: 10        
    }
});

export default Card;
