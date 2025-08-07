package com.felix.countbadhabits.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "MM月dd日";
    public static final String DISPLAY_MONTH_FORMAT = "yyyy年MM月";

    /**
     * 获取今日日期字符串
     */
    public static String getTodayString() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentTimeString() {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String getCurrentDateTimeString() {
        return new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(new Date());
    }

    /**
     * 格式化日期为显示格式
     */
    public static String formatDateForDisplay(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * 格式化月份为显示格式
     */
    public static String formatMonthForDisplay(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        SimpleDateFormat format = new SimpleDateFormat(DISPLAY_MONTH_FORMAT, Locale.getDefault());
        return format.format(calendar.getTime());
    }

    /**
     * 获取指定日期的年份
     */
    public static int getYear(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = format.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    /**
     * 获取指定日期的月份
     */
    public static int getMonth(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = format.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH 从0开始
        } catch (ParseException e) {
            return Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
    }

    /**
     * 获取指定日期的日
     */
    public static int getDay(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = format.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * 获取指定年月的天数
     */
    public static int getDaysInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定年月第一天是星期几（1=星期一，7=星期日）
     */
    public static int getFirstDayOfWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // Calendar.DAY_OF_WEEK: 1=Sunday, 2=Monday, ...
        // 转换为: 1=Monday, 7=Sunday
        return dayOfWeek == 1 ? 7 : dayOfWeek - 1;
    }

    /**
     * 获取上个月
     */
    public static int[] getPreviousMonth(int year, int month) {
        if (month == 1) {
            return new int[]{year - 1, 12};
        } else {
            return new int[]{year, month - 1};
        }
    }

    /**
     * 获取下个月
     */
    public static int[] getNextMonth(int year, int month) {
        if (month == 12) {
            return new int[]{year + 1, 1};
        } else {
            return new int[]{year, month + 1};
        }
    }

    /**
     * 构建日期字符串
     */
    public static String buildDateString(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }

    /**
     * 构建时间字符串
     */
    public static String buildTimeString(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    /**
     * 判断是否为今天
     */
    public static boolean isToday(String dateString) {
        return getTodayString().equals(dateString);
    }

    /**
     * 比较两个日期
     * @return 负数表示date1早于date2，0表示相等，正数表示date1晚于date2
     */
    public static int compareDates(String date1, String date2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);
            return d1.compareTo(d2);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 获取星期几的显示文本
     */
    public static String getDayOfWeekText(int dayOfWeek) {
        String[] days = {"一", "二", "三", "四", "五", "六", "日"};
        if (dayOfWeek >= 1 && dayOfWeek <= 7) {
            return days[dayOfWeek - 1];
        }
        return "";
    }
}