package com.sakaimobile.development.sakaiclient20.persistence.typeconverters;


import java.util.Date;

import androidx.room.TypeConverter;

/**
 * Created by Development on 8/5/18.
 */

public class DateConverter {

    @TypeConverter
    public static Date fromTimestamp(Long timestamp) {
        return timestamp != null ? new Date(timestamp) : null;
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date != null ? date.getTime() : null;
    }
}
