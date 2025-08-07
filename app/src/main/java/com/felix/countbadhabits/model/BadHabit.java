package com.felix.countbadhabits.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 坏习惯实体类
 */
public class BadHabit {
    private long id;
    private String name;
    private int dailyLimit;
    private String createdDate;
    private boolean isActive;

    public BadHabit() {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        this.isActive = true;
        this.dailyLimit = 5; // 默认上限
    }

    public BadHabit(String name, int dailyLimit) {
        this();
        this.name = name;
        this.dailyLimit = dailyLimit;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return name + " (上限:" + dailyLimit + ")";
    }
}