package com.felix.countbadhabits.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 */
public class PreferenceUtils {
    private static final String PREF_NAME = "CountBadHabitsPrefs";
    private static final String KEY_CURRENT_HABIT_ID = "current_habit_id";
    private static final String KEY_CALENDAR_VIEW_MODE = "calendar_view_mode"; // true=月视图, false=周视图
    private static final String KEY_CHART_VIEW_MODE = "chart_view_mode"; // true=月统计, false=年统计

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存当前选中的坏习惯ID
     */
    public static void setCurrentHabitId(Context context, long habitId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(KEY_CURRENT_HABIT_ID, habitId);
        editor.apply();
    }

    /**
     * 获取当前选中的坏习惯ID
     */
    public static long getCurrentHabitId(Context context) {
        return getPreferences(context).getLong(KEY_CURRENT_HABIT_ID, -1);
    }

    /**
     * 保存日历视图模式
     */
    public static void setCalendarViewMode(Context context, boolean isMonthView) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(KEY_CALENDAR_VIEW_MODE, isMonthView);
        editor.apply();
    }

    /**
     * 获取日历视图模式
     */
    public static boolean isCalendarMonthView(Context context) {
        return getPreferences(context).getBoolean(KEY_CALENDAR_VIEW_MODE, true);
    }

    /**
     * 保存图表视图模式
     */
    public static void setChartViewMode(Context context, boolean isMonthChart) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(KEY_CHART_VIEW_MODE, isMonthChart);
        editor.apply();
    }

    /**
     * 获取图表视图模式
     */
    public static boolean isChartMonthView(Context context) {
        return getPreferences(context).getBoolean(KEY_CHART_VIEW_MODE, true);
    }

    /**
     * 清除所有偏好设置
     */
    public static void clearAll(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}