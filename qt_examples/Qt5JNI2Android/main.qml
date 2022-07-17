import QtQuick 2.12
import QtQuick.Window 2.12
import QtQuick.Controls 2.12


Window {
    id: rootComponentId    // Идентификатор корневого элемента
    visible: true
    width: 640
    height: 480
    title: qsTr("Hello World")

    // Определяем сигнал, посредством которого передаём сообщение в C++ код
    signal qmlCallJNISignal()


    Button {
         id: btnMakeRequest
         objectName: "TheButton"
         anchors.left: parent.left
         anchors.top: parent.top
         text: "Call through JNI"

         // Определяем сигнал, который будет сгенерирован
         // при нажатии кнопки
         MouseArea {
             anchors.fill: parent
             onClicked: rootComponentId.qmlCallJNISignal()
         }
    }
}
