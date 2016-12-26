package com.example.dyckster.sebbiatesttask.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by dyckster on 17.11.2016.
 */

public class TimeUtil {
    public static String getFormattedDate(String dateToFormat) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateToFormat);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMMM yyyy 'at' HH:mm");
        return dtf.print(dateTime);
    }
}
