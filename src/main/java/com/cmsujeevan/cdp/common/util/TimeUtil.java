package com.cmsujeevan.cdp.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

    public static final String DATE_TIME_FORMAT_JOB_ID = "yyyy-MM-dd-HH-mm-ss-SSS";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DEFAULT_TIME_ZONE = "GMT";

    public static String currentDate(String format, String timeZone) {
        var dtf = new SimpleDateFormat(format);
        dtf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dtf.format(Calendar.getInstance(TimeZone.getTimeZone(timeZone)).getTime());
    }

    public static Timestamp getCurrentTimestampInTimezone(String timeZone) {
        return Timestamp.valueOf(currentDate(DATE_TIME_FORMAT, timeZone));
    }

    public static long getTimeInMillis(int duration, String timeUnit) {
        long durationInMillis;
        switch (timeUnit) {
            case "minute":
                durationInMillis = duration * 60L;
                break;
            case "hour":
                durationInMillis = duration * 60 * 60L;
                break;
            case "day":
                durationInMillis = duration * 24 * 60 * 60L;
                break;
            default:
                durationInMillis = duration;
                break;
        }
        return durationInMillis * 1000L;
    }
}
