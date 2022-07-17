#include "mypersonallistmodel.h"


MyPersonalListModel::MyPersonalListModel(QObject* parent) :
    QAbstractListModel(parent)
{
}

int MyPersonalListModel::rowCount(const QModelIndex& parent) const
{
    Q_UNUSED(parent);
    return m_Messages.count();
}

// Метод позволяет связать сиволические имена свойств (ролей), которые
// используются в JavaScript-коде QML с числовыми идентификаторами
// полей описания объекта, которые используются в методе data()
QHash<int, QByteArray> MyPersonalListModel::roleNames() const
{
    QHash<int, QByteArray> roles;
    roles[Roles::IdRole] = "id";
    roles[Roles::NameRole] = "name";
    return roles;
}

// Под понятием "роль" подразумевается имя свойства отдельного элемента
// списка. Кроме этого, существует ещё и "отображаемая роль", т.е. главное
// свойство элемента - Qt::DisplayRole
QVariant MyPersonalListModel::data(const QModelIndex& index, int role) const
{
    if (!isIndexValid(index)) {
        return QVariant();
    }

    const MessageEntry &me = m_Messages[index.row()];

    switch(role) {
    case IdRole:
        return QVariant::fromValue(me.mId);
    case NameRole:
    case Qt::DisplayRole:
        return QVariant::fromValue(me.mText);
    }

    return QVariant();
}

bool MyPersonalListModel::isIndexValid(const QModelIndex& index) const
{
    return (index.row() >= 0 && index.row() < m_Messages.size());
}

void MyPersonalListModel::addEntry(MessageEntry _em)
{
    beginInsertRows(QModelIndex(), m_Messages.size(), m_Messages.size());
    m_Messages.append(_em);
    endInsertRows();

    // Приблизительно такой код нужен, когда мы собираемся добавлять
    // данные в контейнер в динамике
    QModelIndex topLeft = createIndex(0, 0);
    emit dataChanged(topLeft, topLeft, {Qt::DisplayRole});
}
