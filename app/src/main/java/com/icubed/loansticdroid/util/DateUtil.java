package com.icubed.loansticdroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String WEEK = "week";

    public static final String PER_YEAR = "per year";
    public static final String PER_MONTH = "per month";
    public static final String PER_DAY = "per day";
    public static final String PER_WEEK = "per week";

    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }
    public static Date addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }
    public static Date addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTime();
    }

    public static String dateString(Date date) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        return sdf.format(date);
    }

    public static Date stringDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.US);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
