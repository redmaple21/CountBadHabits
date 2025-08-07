package com.felix.countbadhabits.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.database.BadHabitDao;
import com.felix.countbadhabits.database.DatabaseHelper;
import com.felix.countbadhabits.fragment.HistoryFragment;
import com.felix.countbadhabits.fragment.SettingsFragment;
import com.felix.countbadhabits.fragment.TodayFragment;
import com.felix.countbadhabits.model.BadHabit;
import com.felix.countbadhabits.utils.PreferenceUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity
 */
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Spinner habitSpinner;
    private Toolbar toolbar;

    private BadHabitDao habitDao;
    private List<BadHabit> habitList;
    private ArrayAdapter<BadHabit> spinnerAdapter;
    private long currentHabitId = -1;

    // Fragment instances
    private TodayFragment todayFragment;
    private HistoryFragment historyFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabase();
        initViews();
        setupToolbar();
        setupHabitSpinner();
        setupBottomNavigation();
        loadDefaultFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从设置页面返回时可能需要刷新习惯列表
        refreshHabitList();
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        habitDao = new BadHabitDao(dbHelper);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        habitSpinner = findViewById(R.id.habit_spinner);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupHabitSpinner() {
        habitList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        habitSpinner.setAdapter(spinnerAdapter);

        habitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < habitList.size()) {
                    BadHabit selectedHabit = habitList.get(position);
                    currentHabitId = selectedHabit.getId();
                    PreferenceUtils.setCurrentHabitId(MainActivity.this, currentHabitId);
                    
                    // 通知所有Fragment更新数据
                    notifyFragmentsHabitChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        loadHabitList();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_today) {
                if (todayFragment == null) {
                    todayFragment = TodayFragment.newInstance(currentHabitId);
                }
                fragment = todayFragment;
            } else if (itemId == R.id.nav_history) {
                if (historyFragment == null) {
                    historyFragment = HistoryFragment.newInstance(currentHabitId);
                }
                fragment = historyFragment;
            } else if (itemId == R.id.nav_settings) {
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }
                fragment = settingsFragment;
            }
            
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            
            return false;
        });
    }

    private void loadDefaultFragment() {
        // 默认显示当日记录
        bottomNavigation.setSelectedItemId(R.id.nav_today);
    }

    private void loadHabitList() {
        habitList.clear();
        List<BadHabit> habits = habitDao.getAllActiveHabits();
        
        if (habits.isEmpty()) {
            // 如果没有习惯，显示提示信息
            Toast.makeText(this, R.string.error_no_habits, Toast.LENGTH_LONG).show();
            return;
        }
        
        habitList.addAll(habits);
        spinnerAdapter.notifyDataSetChanged();
        
        // 设置当前选中的习惯
        long savedHabitId = PreferenceUtils.getCurrentHabitId(this);
        if (savedHabitId == -1) {
            // 如果没有保存的习惯ID，选择第一个
            savedHabitId = habits.get(0).getId();
            PreferenceUtils.setCurrentHabitId(this, savedHabitId);
        }
        
        // 在Spinner中选中对应的习惯
        for (int i = 0; i < habitList.size(); i++) {
            if (habitList.get(i).getId() == savedHabitId) {
                habitSpinner.setSelection(i);
                currentHabitId = savedHabitId;
                break;
            }
        }
    }

    /**
     * 刷新习惯列表（从设置页面调用）
     */
    public void refreshHabitList() {
        loadHabitList();
    }

    /**
     * 通知所有Fragment习惯发生了变化
     */
    private void notifyFragmentsHabitChanged() {
        if (todayFragment != null) {
            todayFragment.onHabitChanged(currentHabitId);
        }
        if (historyFragment != null) {
            historyFragment.onHabitChanged(currentHabitId);
        }
    }

    /**
     * 获取当前选中的习惯ID
     */
    public long getCurrentHabitId() {
        return currentHabitId;
    }

    /**
     * 设置当前选中的习惯
     */
    public void setCurrentHabit(long habitId) {
        this.currentHabitId = habitId;
        PreferenceUtils.setCurrentHabitId(this, habitId);
        
        // 在Spinner中选中对应的习惯
        for (int i = 0; i < habitList.size(); i++) {
            if (habitList.get(i).getId() == habitId) {
                habitSpinner.setSelection(i);
                break;
            }
        }
        
        notifyFragmentsHabitChanged();
    }
}