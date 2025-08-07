package com.felix.countbadhabits.model;

/**
 * 日统计实体类
 */
public class DailySummary {
    private String date;           // YYYY-MM-DD格式
    private int triggerCount;      // 触发次数
    private int dailyLimit;        // 当日上限
    private boolean isExceeded;    // 是否超过上限

    public DailySummary() {
    }

    public DailySummary(String date, int triggerCount, int dailyLimit) {
        this.date = date;
        this.triggerCount = triggerCount;
        this.dailyLimit = dailyLimit;
        this.isExceeded = triggerCount > dailyLimit;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
        this.isExceeded = triggerCount > dailyLimit;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
        this.isExceeded = triggerCount > dailyLimit;
    }

    public boolean isExceeded() {
        return isExceeded;
    }

    public void setExceeded(boolean exceeded) {
        isExceeded = exceeded;
    }

    /**
     * 获取状态颜色资源ID
     */
    public int getStatusColor() {
        return isExceeded ? android.R.color.holo_red_light : android.R.color.holo_green_light;
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        return isExceeded ? "超限" : "正常";
    }
}