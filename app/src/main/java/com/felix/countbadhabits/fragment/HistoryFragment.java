package com.felix.countbadhabits.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.activity.DayDetailActivity;
import com.felix.countbadhabits.database.BadHabitDao;
import com.felix.countbadhabits.database.DatabaseHelper;
import com.felix.countbadhabits.database.TriggerRecordDao;
import com.felix.countbadhabits.model.BadHabit;
import com.felix.countbadhabits.utils.DateUtils;
import com.felix.countbadhabits.utils.PreferenceUtils;
import com.felix.countbadhabits.widget.BarChartView;
import com.felix.countbadhabits.widget.CustomCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 历史记录Fragment
 */
public class HistoryFragment extends Fragment {
    private static final String ARG_HABIT_ID = "habit_id";

    private Button btnPrevMonth, btnNextMonth;
    private TextView tvCurrentMonth;
    private ToggleButton toggleCalendarMode, toggleChartMode;
    private CustomCalendarView calendarView;
    private BarChartView chartView;

    private long habitId;
    private int currentYear;
    private int currentMonth;
    
    private TriggerRecordDao recordDao;
    private BadHabitDao habitDao;

    public static HistoryFragment newInstance(long habitId) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_HABIT_ID, habitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            habitId = getArguments().getLong(ARG_HABIT_ID, -1);
        }
        
        // 初始化当前年月
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        
        initDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        setupClickListeners();
        loadPreferences();
        updateMonthDisplay();
        loadCalendarData();
        loadChartData();
        return view;
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        recordDao = new TriggerRecordDao(dbHelper);
        habitDao = new BadHabitDao(dbHelper);
    }

    private void initViews(View view) {
        btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        btnNextMonth = view.findViewById(R.id.btn_next_month);
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
        toggleCalendarMode = view.findViewById(R.id.toggle_calendar_mode);
        toggleChartMode = view.findViewById(R.id.toggle_chart_mode);
        calendarView = view.findViewById(R.id.calendar_view);
        chartView = view.findViewById(R.id.chart_view);
    }

    private void setupClickListeners() {
        btnPrevMonth.setOnClickListener(v -> {
            int[] prevMonth = DateUtils.getPreviousMonth(currentYear, currentMonth);
            currentYear = prevMonth[0];
            currentMonth = prevMonth[1];
            updateMonthDisplay();
            loadCalendarData();
            loadChartData();
        });

        btnNextMonth.setOnClickListener(v -> {
            int[] nextMonth = DateUtils.getNextMonth(currentYear, currentMonth);
            currentYear = nextMonth[0];
            currentMonth = nextMonth[1];
            updateMonthDisplay();
            loadCalendarData();
            loadChartData();
        });

        toggleCalendarMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            calendarView.setViewMode(isChecked); // true=月视图
            PreferenceUtils.setCalendarViewMode(getContext(), isChecked);
            updateToggleButtonText();
        });

        toggleChartMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceUtils.setChartViewMode(getContext(), isChecked);
            updateToggleButtonText();
            loadChartData();
        });

        calendarView.setOnDateClickListener(date -> {
            if (habitId != -1) {
                Intent intent = DayDetailActivity.newIntent(getContext(), date, habitId);
                startActivity(intent);
            }
        });
    }

    private void loadPreferences() {
        boolean isCalendarMonthView = PreferenceUtils.isCalendarMonthView(getContext());
        boolean isChartMonthView = PreferenceUtils.isChartMonthView(getContext());
        
        toggleCalendarMode.setChecked(isCalendarMonthView);
        toggleChartMode.setChecked(isChartMonthView);
        
        calendarView.setViewMode(isCalendarMonthView);
        updateToggleButtonText();
    }

    private void updateToggleButtonText() {
        toggleCalendarMode.setTextOn(getString(R.string.month_view));
        toggleCalendarMode.setTextOff(getString(R.string.week_view));
        
        toggleChartMode.setTextOn(getString(R.string.monthly_stats));
        toggleChartMode.setTextOff(getString(R.string.yearly_stats));
    }

    private void updateMonthDisplay() {
        String monthText = DateUtils.formatMonthForDisplay(currentYear, currentMonth);
        tvCurrentMonth.setText(monthText);
        
        calendarView.setYearMonth(currentYear, currentMonth);
    }

    private void loadCalendarData() {
        if (habitId == -1) return;

        Map<String, Integer> monthlyData = recordDao.getMonthlyStatistics(habitId, currentYear, currentMonth);
        BadHabit habit = habitDao.getHabitById(habitId);
        int dailyLimit = habit != null ? habit.getDailyLimit() : 5;
        
        calendarView.setDailyData(monthlyData, dailyLimit);
    }

    private void loadChartData() {
        if (habitId == -1) return;

        List<BarChartView.ChartData> chartData = new ArrayList<>();
        boolean isMonthChart = toggleChartMode.isChecked();
        
        if (isMonthChart) {
            // 月统计：显示当月每周的数据
            loadWeeklyChartData(chartData);
        } else {
            // 年统计：显示当年每月的数据
            loadMonthlyChartData(chartData);
        }
        
        chartView.setData(chartData, isMonthChart);
    }

    private void loadWeeklyChartData(List<BarChartView.ChartData> chartData) {
        Map<String, Integer> monthlyData = recordDao.getMonthlyStatistics(habitId, currentYear, currentMonth);
        
        // 计算每周的统计数据
        int[] weeklyStats = new int[6]; // 最多6周
        
        for (Map.Entry<String, Integer> entry : monthlyData.entrySet()) {
            String dateStr = entry.getKey();
            int day = DateUtils.getDay(dateStr);
            int firstDayOfWeek = DateUtils.getFirstDayOfWeek(currentYear, currentMonth);
            
            // 计算这一天属于第几周
            int weekNumber = ((day + firstDayOfWeek - 2) / 7);
            if (weekNumber < weeklyStats.length) {
                weeklyStats[weekNumber] += entry.getValue();
            }
        }
        
        // 添加到图表数据
        for (int i = 0; i < weeklyStats.length; i++) {
            if (weeklyStats[i] > 0) {
                chartData.add(new BarChartView.ChartData("第" + (i + 1) + "周", weeklyStats[i]));
            }
        }
    }

    private void loadMonthlyChartData(List<BarChartView.ChartData> chartData) {
        Map<String, Integer> yearlyData = recordDao.getYearlyStatistics(habitId, currentYear);
        
        // 按月份顺序添加数据
        String[] monthNames = {"1月", "2月", "3月", "4月", "5月", "6月", 
                              "7月", "8月", "9月", "10月", "11月", "12月"};
        
        for (int i = 1; i <= 12; i++) {
            String monthKey = String.format("%04d-%02d", currentYear, i);
            Integer count = yearlyData.get(monthKey);
            if (count != null && count > 0) {
                chartData.add(new BarChartView.ChartData(monthNames[i - 1], count));
            }
        }
    }

    /**
     * 当习惯发生变化时调用
     */
    public void onHabitChanged(long newHabitId) {
        this.habitId = newHabitId;
        if (calendarView != null && chartView != null) {
            loadCalendarData();
            loadChartData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从日详情页面返回时刷新数据
        if (calendarView != null && chartView != null) {
            loadCalendarData();
            loadChartData();
        }
    }
}