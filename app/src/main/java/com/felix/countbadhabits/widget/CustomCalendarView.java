package com.felix.countbadhabits.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.utils.DateUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义日历控件
 */
public class CustomCalendarView extends View {
    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint headerPaint;
    
    private int year;
    private int month;
    private int daysInMonth;
    private int firstDayOfWeek;
    private boolean isMonthView = true; // true=月视图，false=周视图
    
    private Map<String, Integer> dailyCounts; // 日期 -> 触发次数
    private Map<String, Boolean> dailyStatus; // 日期 -> 是否超限
    private int dailyLimit = 5; // 默认上限
    
    private int cellWidth;
    private int cellHeight;
    private int headerHeight;
    
    private OnDateClickListener dateClickListener;
    
    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        textPaint.setTextSize(48f);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setTextAlign(Paint.Align.CENTER);
        headerPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        headerPaint.setTextSize(42f);

        // 初始化数据
        dailyCounts = new HashMap<>();
        dailyStatus = new HashMap<>();
        
        // 设置当前月份
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        
        updateCalendarData();
    }

    private void updateCalendarData() {
        daysInMonth = DateUtils.getDaysInMonth(year, month);
        firstDayOfWeek = DateUtils.getFirstDayOfWeek(year, month);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        cellWidth = w / 7;
        headerHeight = 80;
        
        if (isMonthView) {
            cellHeight = (h - headerHeight) / 6; // 6行
        } else {
            cellHeight = (h - headerHeight) / 2; // 2行（周视图）
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        drawHeader(canvas);
        drawCalendarGrid(canvas);
        drawDatesAndCounts(canvas);
    }

    private void drawHeader(Canvas canvas) {
        String[] weekDays = {"一", "二", "三", "四", "五", "六", "日"};
        
        for (int i = 0; i < 7; i++) {
            float x = i * cellWidth + cellWidth / 2f;
            float y = headerHeight / 2f + headerPaint.getTextSize() / 3f;
            canvas.drawText(weekDays[i], x, y, headerPaint);
        }
    }

    private void drawCalendarGrid(Canvas canvas) {
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        backgroundPaint.setStrokeWidth(2f);
        
        int totalRows = isMonthView ? 6 : 2;
        
        // 绘制水平线
        for (int i = 0; i <= totalRows; i++) {
            float y = headerHeight + i * cellHeight;
            canvas.drawLine(0, y, getWidth(), y, backgroundPaint);
        }
        
        // 绘制垂直线
        for (int i = 0; i <= 7; i++) {
            float x = i * cellWidth;
            canvas.drawLine(x, headerHeight, x, getHeight(), backgroundPaint);
        }
    }

    private void drawDatesAndCounts(Canvas canvas) {
        int day = 1;
        int totalRows = isMonthView ? 6 : 2;
        
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < 7; col++) {
                // 第一行需要根据月份第一天是星期几来调整
                if (row == 0 && col < firstDayOfWeek - 1) {
                    continue;
                }
                
                if (day > daysInMonth) {
                    break;
                }
                
                drawDayCell(canvas, row, col, day);
                day++;
            }
        }
    }

    private void drawDayCell(Canvas canvas, int row, int col, int day) {
        float centerX = col * cellWidth + cellWidth / 2f;
        float centerY = headerHeight + row * cellHeight + cellHeight / 2f;
        
        String dateString = DateUtils.buildDateString(year, month, day);
        Integer count = dailyCounts.get(dateString);
        Boolean isExceeded = dailyStatus.get(dateString);
        
        if (count == null) count = 0;
        if (isExceeded == null) isExceeded = false;
        
        // 绘制背景
        if (count > 0) {
            backgroundPaint.setColor(isExceeded ? 
                ContextCompat.getColor(getContext(), R.color.status_exceeded) : 
                ContextCompat.getColor(getContext(), R.color.status_normal));
            canvas.drawCircle(centerX, centerY, cellWidth * 0.3f, backgroundPaint);
        }
        
        // 绘制日期数字
        textPaint.setColor(count > 0 ? 
            ContextCompat.getColor(getContext(), android.R.color.white) : 
            ContextCompat.getColor(getContext(), R.color.text_primary));
        
        // 今天的日期加粗显示
        if (DateUtils.isToday(dateString)) {
            textPaint.setFakeBoldText(true);
        } else {
            textPaint.setFakeBoldText(false);
        }
        
        canvas.drawText(String.valueOf(day), centerX, centerY + textPaint.getTextSize() / 3f, textPaint);
        
        // 绘制触发次数
        if (count > 0) {
            textPaint.setTextSize(32f);
            textPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
            canvas.drawText(String.valueOf(count), centerX, centerY + cellHeight * 0.25f, textPaint);
            textPaint.setTextSize(48f); // 恢复原始大小
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            
            if (y > headerHeight) {
                String clickedDate = getDateFromCoordinates(x, y);
                if (clickedDate != null && dateClickListener != null) {
                    dateClickListener.onDateClick(clickedDate);
                }
            }
        }
        return true;
    }

    private String getDateFromCoordinates(float x, float y) {
        int col = (int) (x / cellWidth);
        int row = (int) ((y - headerHeight) / cellHeight);
        
        if (col < 0 || col >= 7 || row < 0) {
            return null;
        }
        
        int day = row * 7 + col - (firstDayOfWeek - 2);
        
        if (day >= 1 && day <= daysInMonth) {
            return DateUtils.buildDateString(year, month, day);
        }
        
        return null;
    }

    // 公共方法
    
    public void setYear(int year) {
        this.year = year;
        updateCalendarData();
        invalidate();
    }

    public void setMonth(int month) {
        this.month = month;
        updateCalendarData();
        invalidate();
    }

    public void setYearMonth(int year, int month) {
        this.year = year;
        this.month = month;
        updateCalendarData();
        invalidate();
    }

    public void setViewMode(boolean isMonthView) {
        this.isMonthView = isMonthView;
        requestLayout();
        invalidate();
    }

    public void setDailyData(Map<String, Integer> dailyCounts, int dailyLimit) {
        this.dailyCounts.clear();
        this.dailyStatus.clear();
        this.dailyLimit = dailyLimit;
        
        if (dailyCounts != null) {
            this.dailyCounts.putAll(dailyCounts);
            
            // 计算是否超限
            for (Map.Entry<String, Integer> entry : dailyCounts.entrySet()) {
                this.dailyStatus.put(entry.getKey(), entry.getValue() > dailyLimit);
            }
        }
        
        invalidate();
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.dateClickListener = listener;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public boolean isMonthView() {
        return isMonthView;
    }
}