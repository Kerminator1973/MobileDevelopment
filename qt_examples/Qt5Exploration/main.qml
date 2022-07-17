import QtQuick 2.11
import QtQuick.Window 2.11
import QtQuick.Controls 2.11

Window {
    id : rootComponentId    // Идентификатор корневого элемента
    visible: true
    width: 600
    height: 800
    title: qsTr("Signals and Slots Demo")

    // Определяем сигнал, посредством которого передаём сообщение в C++ код
    signal qmlSignal(string msg)
    signal qmlDoRequestSignal()

    // Определяем слот, который будет получать информацию от MButtons
    // и пересылать этот сигнал дальше по цепочке
    function receiveInfo(_count) {
        qmlSignal("The count is " + _count)
    }

    function logReceivedList(someArray) {

        console.log("Some list is received");
        for (var i=0; i < someArray.length; i++) {
            console.log("Array item:", someArray[i]);
            listModel.addElementToList(someArray[i]);
        }
    }

    function logReceivedComplexList(someArray) {
        console.log("Some complex list is received");
        for (var i=0; i < someArray.length; i++) {
            console.log("Item: {" + someArray[i].val + ", '" + someArray[i].name1 + "', '" + someArray[i].name2 + "'}");
            listModel.addElementToList(someArray[i].name1);
        }
    }

    // Разбиваем органы управления на три отдельных элемента, переключаться
    // между которыми можно используя жест "смашивание" / "swipe"
    SwipeView {
        id: view    // Это свойство используется объектом PageIndicator

        currentIndex: 1
        anchors.fill: parent

        Item {
            id: firstPage
            objectName: "firstPageInSwipeView"

            // Определяем две кнопки, сообщения о нажатии которых
            // переадрессуем друг другу
            MButton {
                id : leftButtonId
                rectColor: "yellowgreen"

                // Связывание сигнала и слота может быть выполнено через свойство
                // (в данном примере это могло бы быть свойство target), либо
                // явным образом, через уведомление onCompleted
                //target: rightButtonId
            }

            MButton {
                id : rightButtonId
                objectName: "TheSecondButton"
                rectColor: "dodgerblue"
                anchors.right: parent.right
                //target: leftButtonId

                function logUsefulInfo(msg) {
                    console.log("It is useful information: " + msg);
                    return 55;
                }
            }

            // Создаём дополнительную кнопку, при нажатии на которую
            // будет сформирован запрос на сервер
            Button {
                 id: btnMakeRequest
                 objectName: "TheThirdButton"
                 anchors.left: rightButtonId.left
                 anchors.top: rightButtonId.bottom
                 text: "Make a Request"

                 // Определяем сигнал, который будет сгенерирован
                 // при нажатии кнопки
                 MouseArea {
                     anchors.fill: parent
                     onClicked: rootComponentId.qmlDoRequestSignal()
                 }
            }
        }
        Item {
            id: secondPage

            // ListView и ListModel используются для демонстрации возможности
            // динамически добавлять элементы в список
            ListView {
                id: listView
                width: parent.width
                height: parent.height

                model: listModel
                delegate: Rectangle {
                    width: listView.width
                    height: listView.height / 4
                    color: "yellow"

                    Text {
                        text: hour  // Текст берём из свойства "hour"
                        anchors.centerIn: parent
                    }
                }
            }

            ListModel {
                id: listModel
                Component.onCompleted: {
                    // Пока ничего не добавляем
                }

                function addElementToList(element) {
                    // Добавляем в список пару key-value, в котором ключем
                    // является поле "hour", а значение передаётся из вне
                    append( {hour: element.toString()} );
                }
            }
        }
        Item {
            id: thirdPage

            // Список передаётся из C++ кода
            ListView {
                id: quickMessageListData        // Идентификатор органа управления
                model: quickListModel           // Под этим именем будет определёна модель в вызове setContextProperty()
                width: parent.width
                height: parent.height

                delegate: Rectangle {           // Определяем описание отдельных строк списка

                    width: 600                  // Геометрические размеры
                    height: 120
                    color: "white"              // Цвет фона
                    Text {                      // Текст, комбинированный из нескольких элементов
                        text: model.name + " (" + model.id + ")"

                        font.family: "Helvetica"
                        font.pointSize: 32
                        font.bold: true
                    }
                }
            }
        }
    }

    // PageIndicator используется совместно с SwipeView
    // и позволяет понять, какой из Item-ов SwipeView
    // отображается на экране в данный в момент
    PageIndicator {
        id: indicator

        count: view.count
        currentIndex: view.currentIndex

        anchors.bottom: view.bottom
        anchors.horizontalCenter: parent.horizontalCenter
    }

    Component.onCompleted: {
        // Связываем сигналы и слоты каждой из кнопок друг с другом
        leftButtonId.notify.connect(rightButtonId.receiveInfo)
        rightButtonId.notify.connect(leftButtonId.receiveInfo)

        // Дополнительно связываем сигналы
        leftButtonId.notify.connect(rootComponentId.receiveInfo)
        rightButtonId.notify.connect(rootComponentId.receiveInfo)

        // Работает с общей структурой, через конкретный объект,
        // созданный в коде C++
        console.log("classObj.sharedStruct.val = " + classObj.sharedStruct.val)
        console.log("name1 = '" + classObj.sharedStruct.name1 + "'")
        console.log("name2 = '" + classObj.sharedStruct.name2 + "'")

        classObj.sharedStruct.name1 = "Bob"
        classObj.sharedStruct.name2 = "Dilan"
    }
}


