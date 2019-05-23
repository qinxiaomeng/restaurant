package com.ej.restaurant.utils;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DLClock {
    private DLClock() {
    }

    public static final String TIME_ZONE = "Asia/Shanghai";
    public static final String FORMAT_YYYY = "yyyy";
    public static final String FORMAT_MM = "MM";
    public static final String FORMAT_dd = "dd";
    public static final String FORMAT_YYYY_MM_dd = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMdd = "yyyyMMdd";
    public static final String FORMAT_YYYYMMddHHmmss = "yyyyMMddHHmmss";

    public static DateTime nowDate() {
        return new DateTime(getTimeZone());
    }

    public static long now() {
        return nowDate().getMillis();
    }
    public static long today() {
        return nowDate().millisOfDay().withMinimumValue().getMillis();
    }

    public static DateTimeZone getTimeZone() {
        return DateTimeZone.forID(TIME_ZONE);
    }


    public static DateTime date(Long timestamp) {
        return new DateTime(timestamp, getTimeZone());
    }

    public static String tsToString(Long timestamp) {
        return tsToString(timestamp, FORMAT_YYYY_MM_dd_HH_mm_ss);
    }

    public static String tsToString(Long timestamp, String format) {
        if (null == timestamp) {
            return null;
        }
        return date(timestamp).toString(DateTimeFormat.forPattern(format));
    }

    public static String tsToString(Long timestamp, DateTimeFormatter format) {
        if (null == timestamp) {
            return null;
        }
        return date(timestamp).toString(format);
    }

    public static DateTime parse(String dateTimeStr, String format) {
        return DateTimeFormat.forPattern(format).withZone(getTimeZone()).parseDateTime(dateTimeStr);
    }
}
