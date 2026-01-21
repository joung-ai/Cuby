package com.example.cuby.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public static String getMonthPattern(int year, int month) {
        return String.format(Locale.getDefault(), "%04d-%02d%%", year, month);
    }
    
    public static String formatFriendly(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            Date date = input.parse(dateStr);
            return date != null ? output.format(date) : dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }
}
