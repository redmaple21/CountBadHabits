package com.felix.countbadhabits.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 触发记录实体类
 */
public class TriggerRecord {
    private long id;
    private long habitId;
    private String triggerDate;        // YYYY-MM-DD格式
    private String triggerTime;        // HH:MM格式
    private String triggerDateTime;    // YYYY-MM-DD HH:MM:SS格式
    private String description;
    private int sequenceNumber;        // 当日序号

    public TriggerRecord() {
        Date now = new Date();
        this.triggerDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
        this.triggerTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
        this.triggerDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(now);
        this.description = "";
    }

    public TriggerRecord(long habitId, String description) {
        this();
        this.habitId = habitId;
        this.description = description;
    }

    public TriggerRecord(long habitId, String triggerTime, String description) {
        this(habitId, description);
        this.triggerTime = triggerTime;
        // 更新完整的日期时间
        updateTriggerDateTime();
    }

    private void updateTriggerDateTime() {
        this.triggerDateTime = this.triggerDate + " " + this.triggerTime + ":00";
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHabitId() {
        return habitId;
    }

    public void setHabitId(long habitId) {
        this.habitId = habitId;
    }

    public String getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(String triggerDate) {
        this.triggerDate = triggerDate;
        updateTriggerDateTime();
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
        updateTriggerDateTime();
    }

    public String getTriggerDateTime() {
        return triggerDateTime;
    }

    public void setTriggerDateTime(String triggerDateTime) {
        this.triggerDateTime = triggerDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * 获取显示用的时间格式
     */
    public String getDisplayTime() {
        return triggerTime;
    }

    /**
     * 获取显示用的描述，如果为空则返回默认文本
     */
    public String getDisplayDescription() {
        return description != null && !description.trim().isEmpty() ? description : "无具体描述";
    }
}