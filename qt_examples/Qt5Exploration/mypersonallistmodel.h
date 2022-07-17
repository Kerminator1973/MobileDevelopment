#ifndef MYPERSONALLISTMODEL_H
#define MYPERSONALLISTMODEL_H

#include <QAbstractListModel>
#include <QObject>


// Вспомогательный класс, содержит список значений свойств, отображаемых в QML
class MessageEntry {
    //Q_OBJECT  - не нужно определять, т.к. класс не используется в signal/slot
public:
    MessageEntry(QString _id, QString _text) :
        mId(_id),
        mText(_text)
    {}

    virtual ~MessageEntry()
    {}

    QString mId;
    QString mText;
};

class MyPersonalListModel : public QAbstractListModel
{
    Q_OBJECT
public:
    // Список ролей - конкретных свойств элемента, который является контейнером данных
    // для отображения в списке в QML
    enum Roles {
        IdRole = Qt::UserRole + 1,
        NameRole
    };

    MyPersonalListModel(QObject* parent = nullptr);

    // Обязательный для переопределения метод QAbstractListModel. Возвращает
    // количество элементов в контейнере
    int rowCount(const QModelIndex& parent = QModelIndex()) const override;

    // Обязательный для переопределения метод QAbstractListModel. Возвращает
    // конкретный элемент контейнера
    QVariant data(const QModelIndex& index, int role = Qt::DisplayRole) const override;

    // Обязательный для переопределения метод QAbstractListModel. Позволяет
    // использовать в QML исмволические имена свойств элементов контейнера
    QHash<int, QByteArray> roleNames() const override;

private:
    // Для удобства - проверка корректности переданного индекса
    bool isIndexValid(const QModelIndex& index) const;

public:
    // Для удобства - метод добавления элементов в список
    void addEntry(MessageEntry _em);

private:
    QList<MessageEntry> m_Messages;
};


#endif // MYPERSONALLISTMODEL_H
