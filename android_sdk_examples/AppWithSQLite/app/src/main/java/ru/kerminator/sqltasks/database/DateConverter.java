package ru.kerminator.sqltasks.database;

import androidx.room.TypeConverter;
import java.util.Date;

// DateConverter необходим для преобразования типов Java в типы базы данных и наоборот
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
