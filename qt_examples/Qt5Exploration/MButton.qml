import QtQuick 2.11

// Компонент является источником сигналов, а также содержит слот.
// При нажатии кнопки мыши, передаёт соответствующий сигнал наружу.
// При получении сигнала, увеличивает свойство count на единицу
Item {

    // Свойства, через которые пользовательский код настраивает
    // элемент
    property alias rectColor: componentRectId.color
    width: componentRectId.width
    height: componentRectId.height

    // Свойство, отображаемое в элементе пользовательского интерфейса
    property int count: 0

    // В QML сигнал и слот могут быть связана через специальное свойство
    // в custom component. Тип свойства я указал как var, т.е. использование
    // MButton приведёт к появлению рекурсии и приложение не сможет быть собрано
    /*
    property var target: null
    onTargetChanged: {
        notify.connect(target.receiveInfo)
    }
    */

    // Декларация сигнала
    signal notify(string count)

    // Слот, обрабатывающий сигнал об изменении счётчика
    function receiveInfo(_count){

        componentTextId.text = _count
        count = _count
    }

    // Визуализационная часть компонента
    Rectangle {
        id : componentRectId

        // Значения "по умолчанию". Могут быть изменены через свойства
        // родительского элемента
        width: 200
        height: 200
        color: "red"

        Text {
            id : componentTextId
            anchors.centerIn: parent
            font.pointSize: 20
            text : "0"
        }

        MouseArea{
            anchors.fill: parent
            onClicked: {
                count++
                notify(count)
            }
        }
    }
}
