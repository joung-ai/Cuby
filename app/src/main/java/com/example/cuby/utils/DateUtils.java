package com.example.cuby.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    
    public static String getMonthPattern(Date date) {
        return new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date);
    }
    
    public static String formatFriendly(String isoDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(isoDate);
            return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }
}
