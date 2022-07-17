import QtQuick 2.11

Item {

    // Свойства, через которые пользовательский код настраивает элемент
    width: componentRectId.width
    height: componentRectId.height

    // Визуализационная часть компонента
    Rectangle {
        id : componentRectId

        // Значения "по умолчанию". Могут быть изменены через свойства
        // родительского элемента
        width: 300
        height: 100
        color: "green"

        Text {
            id : componentTextId
            anchors.centerIn: parent
            font.pointSize: 20
            text : "I'm a rect!"
        }
    }
}
