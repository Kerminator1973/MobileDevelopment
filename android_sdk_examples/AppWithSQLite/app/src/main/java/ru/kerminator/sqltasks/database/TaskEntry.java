package ru.kerminator.sqltasks.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

// Описание таблицы базы данных
@Entity(tableName = "task")
public class TaskEntry {

    // Поле "id" - основной ключ, auto increment
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Поле для хранения сгенерированных случайным образом данных
    private String description;

    // Поле для хранения временного штампа, по которому может
    // осуществляться контроль хранения данных в базе
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Конструктор используется для создания новой записи. По каким-то
    // причинам (TODO: нужно разобраться!), для Room нужно применять к
    // этому конструктору макрос @Ignore
    @Ignore
    public TaskEntry(String description, Date updatedAt) {
        this.description = description;
        this.updatedAt = updatedAt;
    }

    // Конструктор для создания объекта, который будет считываться из записи базы данных
    public TaskEntry(int id, String description, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    // Далее следуют setter-ы и getter-ы

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
