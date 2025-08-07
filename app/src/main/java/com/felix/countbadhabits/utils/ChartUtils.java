package com.felix.countbadhabits.utils;

import com.felix.countbadhabits.widget.BarChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图表工具类
 */
public class ChartUtils {

    /**
     * 将月度统计数据转换为周统计图表数据
     */
    public static List<BarChartView.ChartData> convertToWeeklyChartData(
            Map<String, Integer> monthlyData, int year, int month) {
        List<BarChartView.ChartData> chartData = new ArrayList<>();
        
        // 计算每周的统计数据
        int[] weeklyStats = new int[6]; // 最多6周
        
        for (Map.Entry<String, Integer> entry : monthlyData.entrySet()) {
            String dateStr = entry.getKey();
            int day = DateUtils.getDay(dateStr);
            int firstDayOfWeek = DateUtils.getFirstDayOfWeek(year, month);
            
            // 计算这一天属于第几周
            int weekNumber = ((day + firstDayOfWeek - 2) / 7);
            if (weekNumber < weeklyStats.length && weekNumber >= 0) {
                weeklyStats[weekNumber] += entry.getValue();
            }
        }
        
        // 添加到图表数据
        for (int i = 0; i < weeklyStats.length; i++) {
            if (weeklyStats[i] > 0) {
                chartData.add(new BarChartView.ChartData("第" + (i + 1) + "周", weeklyStats[i]));
            }
        }
        
        return chartData;
    }

    /**
     * 将年度统计数据转换为月统计图表数据
     */
    public static List<BarChartView.ChartData> convertToMonthlyChartData(
            Map<String, Integer> yearlyData, int year) {
        List<BarChartView.ChartData> chartData = new ArrayList<>();
        
        // 按月份顺序添加数据
        String[] monthNames = {"1月", "2月", "3月", "4月", "5月", "6月", 
                              "7月", "8月", "9月", "10月", "11月", "12月"};
        
        for (int i = 1; i <= 12; i++) {
            String monthKey = String.format("%04d-%02d", year, i);
            Integer count = yearlyData.get(monthKey);
            if (count != null && count > 0) {
                chartData.add(new BarChartView.ChartData(monthNames[i - 1], count));
            }
        }
        
        return chartData;
    }

    /**
     * 计算数据的最大值
     */
    public static int getMaxValue(List<BarChartView.ChartData> chartData) {
        int maxValue = 0;
        for (BarChartView.ChartData data : chartData) {
            if (data.getValue() > maxValue) {
                maxValue = data.getValue();
            }
        }
        return maxValue;
    }

    /**
     * 判断图表数据是否为空
     */
    public static boolean isEmpty(List<BarChartView.ChartData> chartData) {
        return chartData == null || chartData.isEmpty() || getMaxValue(chartData) == 0;
    }
}