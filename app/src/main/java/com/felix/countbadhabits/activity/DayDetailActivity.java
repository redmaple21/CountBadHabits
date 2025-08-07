package com.felix.countbadhabits.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.adapter.TriggerRecordAdapter;
import com.felix.countbadhabits.database.BadHabitDao;
import com.felix.countbadhabits.database.DatabaseHelper;
import com.felix.countbadhabits.database.TriggerRecordDao;
import com.felix.countbadhabits.model.BadHabit;
import com.felix.countbadhabits.model.TriggerRecord;
import com.felix.countbadhabits.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 单日详情Activity
 */
public class DayDetailActivity extends AppCompatActivity {
    private static final String EXTRA_DATE = "extra_date";
    private static final String EXTRA_HABIT_ID = "extra_habit_id";

    private Toolbar toolbar;
    private TextView tvDateTitle;
    private TextView tvTotalCount;
    private TextView tvNoRecords;
    private RecyclerView rvRecords;

    private String date;
    private long habitId;
    private TriggerRecordDao recordDao;
    private BadHabitDao habitDao;
    private TriggerRecordAdapter adapter;
    private List<TriggerRecord> recordList;

    public static Intent newIntent(Context context, String date, long habitId) {
        Intent intent = new Intent(context, DayDetailActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_HABIT_ID, habitId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        getIntentData();
        initDatabase();
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadDayRecords();
    }

    private void getIntentData() {
        date = getIntent().getStringExtra(EXTRA_DATE);
        habitId = getIntent().getLongExtra(EXTRA_HABIT_ID, -1);

        if (date == null || habitId == -1) {
            finish();
            return;
        }
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        recordDao = new TriggerRecordDao(dbHelper);
        habitDao = new BadHabitDao(dbHelper);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvDateTitle = findViewById(R.id.tv_date_title);
        tvTotalCount = findViewById(R.id.tv_total_count);
        tvNoRecords = findViewById(R.id.tv_no_records);
        rvRecords = findViewById(R.id.rv_records);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 设置日期标题
        String displayDate = DateUtils.formatDateForDisplay(date);
        String today = DateUtils.getTodayString();
        
        if (date.equals(today)) {
            tvDateTitle.setText(getString(R.string.today) + " (" + displayDate + ")");
        } else {
            tvDateTitle.setText(displayDate);
        }
    }

    private void setupRecyclerView() {
        recordList = new ArrayList<>();
        adapter = new TriggerRecordAdapter(recordList, new TriggerRecordAdapter.OnRecordClickListener() {
            @Override
            public void onEditRecord(TriggerRecord record) {
                // 在详情页面不允许编辑，只显示
            }

            @Override
            public void onDeleteRecord(TriggerRecord record) {
                // 在详情页面不允许删除，只显示
            }
        });
        
        rvRecords.setLayoutManager(new LinearLayoutManager(this));
        rvRecords.setAdapter(adapter);
    }

    private void loadDayRecords() {
        recordList.clear();
        List<TriggerRecord> dayRecords = recordDao.getRecordsByDate(habitId, date);
        recordList.addAll(dayRecords);
        adapter.notifyDataSetChanged();

        updateSummary();
        updateEmptyView();
    }

    private void updateSummary() {
        BadHabit habit = habitDao.getHabitById(habitId);
        int totalCount = recordList.size();
        
        tvTotalCount.setText(getString(R.string.total_count, totalCount));
        
        if (habit != null && totalCount > habit.getDailyLimit()) {
            tvTotalCount.setTextColor(getResources().getColor(R.color.status_exceeded));
        } else {
            tvTotalCount.setTextColor(getResources().getColor(R.color.status_normal));
        }
    }

    private void updateEmptyView() {
        if (recordList.isEmpty()) {
            tvNoRecords.setVisibility(android.view.View.VISIBLE);
            rvRecords.setVisibility(android.view.View.GONE);
        } else {
            tvNoRecords.setVisibility(android.view.View.GONE);
            rvRecords.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}