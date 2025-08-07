package com.felix.countbadhabits.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.felix.countbadhabits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图控件
 */
public class BarChartView extends View {
    private Paint barPaint;
    private Paint textPaint;
    private Paint axisPaint;
    
    private List<ChartData> chartData;
    private boolean isMonthlyView = true; // true=月统计，false=年统计
    
    private int padding = 60;
    private int barWidth;
    private int maxValue = 0;

    public static class ChartData {
        private String label; // 月份或周数
        private int value;    // 触发次数
        private String fullLabel; // 完整标签（用于显示）

        public ChartData(String label, int value) {
            this.label = label;
            this.value = value;
            this.fullLabel = label;
        }

        public ChartData(String label, int value, String fullLabel) {
            this.label = label;
            this.value = value;
            this.fullLabel = fullLabel;
        }

        // Getters
        public String getLabel() { return label; }
        public int getValue() { return value; }
        public String getFullLabel() { return fullLabel; }
    }

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(ContextCompat.getColor(getContext(), R.color.purple_500));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        axisPaint.setStrokeWidth(3f);

        chartData = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (chartData == null || chartData.isEmpty()) {
            drawEmptyState(canvas);
            return;
        }

        calculateDimensions();
        drawAxes(canvas);
        drawBars(canvas);
        drawLabels(canvas);
        drawValues(canvas);
    }

    private void drawEmptyState(Canvas canvas) {
        String text = getContext().getString(R.string.no_data);
        textPaint.setTextSize(48f);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        
        float x = getWidth() / 2f;
        float y = getHeight() / 2f + bounds.height() / 2f;
        
        canvas.drawText(text, x, y, textPaint);
    }

    private void calculateDimensions() {
        if (chartData.isEmpty()) return;

        maxValue = 0;
        for (ChartData data : chartData) {
            if (data.getValue() > maxValue) {
                maxValue = data.getValue();
            }
        }

        if (maxValue == 0) maxValue = 1; // 避免除零

        int availableWidth = getWidth() - 2 * padding;
        barWidth = availableWidth / chartData.size() - 20; // 20为间距
    }

    private void drawAxes(Canvas canvas) {
        int chartHeight = getHeight() - 2 * padding;
        
        // Y轴
        canvas.drawLine(padding, padding, padding, getHeight() - padding, axisPaint);
        
        // X轴
        canvas.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding, axisPaint);
        
        // Y轴刻度
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        
        int steps = Math.min(5, maxValue);
        for (int i = 0; i <= steps; i++) {
            int value = (maxValue * i) / steps;
            float y = getHeight() - padding - (chartHeight * i) / (float) steps;
            
            canvas.drawText(String.valueOf(value), padding - 20, y + textPaint.getTextSize() / 3f, textPaint);
            
            // 网格线
            if (i > 0) {
                axisPaint.setColor(ContextCompat.getColor(getContext(), R.color.background_light));
                canvas.drawLine(padding, y, getWidth() - padding, y, axisPaint);
                axisPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
            }
        }
    }

    private void drawBars(Canvas canvas) {
        int chartHeight = getHeight() - 2 * padding;
        float startX = padding + 30; // 30为左边距
        
        for (int i = 0; i < chartData.size(); i++) {
            ChartData data = chartData.get(i);
            
            float barHeight = (chartHeight * data.getValue()) / (float) maxValue;
            float left = startX + i * (barWidth + 20);
            float top = getHeight() - padding - barHeight;
            float right = left + barWidth;
            float bottom = getHeight() - padding;
            
            // 渐变色效果
            if (data.getValue() > 0) {
                barPaint.setColor(ContextCompat.getColor(getContext(), R.color.purple_500));
                canvas.drawRect(left, top, right, bottom, barPaint);
            }
        }
    }

    private void drawLabels(Canvas canvas) {
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        
        float startX = padding + 30;
        
        for (int i = 0; i < chartData.size(); i++) {
            ChartData data = chartData.get(i);
            
            float x = startX + i * (barWidth + 20) + barWidth / 2f;
            float y = getHeight() - padding + 40;
            
            canvas.drawText(data.getLabel(), x, y, textPaint);
        }
    }

    private void drawValues(Canvas canvas) {
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        
        int chartHeight = getHeight() - 2 * padding;
        float startX = padding + 30;
        
        for (int i = 0; i < chartData.size(); i++) {
            ChartData data = chartData.get(i);
            
            if (data.getValue() > 0) {
                float barHeight = (chartHeight * data.getValue()) / (float) maxValue;
                float x = startX + i * (barWidth + 20) + barWidth / 2f;
                float y = getHeight() - padding - barHeight / 2f + textPaint.getTextSize() / 3f;
                
                // 如果柱子太短，在柱子上方显示数值
                if (barHeight < 50) {
                    y = getHeight() - padding - barHeight - 20;
                    textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                } else {
                    textPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
                }
                
                canvas.drawText(String.valueOf(data.getValue()), x, y, textPaint);
            }
        }
    }

    // 公共方法
    
    public void setData(List<ChartData> data, boolean isMonthlyView) {
        this.chartData = data != null ? new ArrayList<>(data) : new ArrayList<>();
        this.isMonthlyView = isMonthlyView;
        invalidate();
    }

    public void setData(List<ChartData> data) {
        setData(data, this.isMonthlyView);
    }

    public boolean isMonthlyView() {
        return isMonthlyView;
    }

    public void clearData() {
        chartData.clear();
        invalidate();
    }
}