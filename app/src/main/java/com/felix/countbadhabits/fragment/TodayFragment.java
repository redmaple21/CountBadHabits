package com.felix.countbadhabits.fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 当日记录Fragment
 */
public class TodayFragment extends Fragment implements TriggerRecordAdapter.OnRecordClickListener {
    private static final String ARG_HABIT_ID = "habit_id";

    private TextView tvCurrentCount, tvDailyLimit, tvStatus, tvNoRecords;
    private CardView statusCard;
    private RecyclerView rvRecords;
    private FloatingActionButton fabAdd;

    private long habitId;
    private TriggerRecordDao recordDao;
    private BadHabitDao habitDao;
    private TriggerRecordAdapter adapter;
    private List<TriggerRecord> recordList;

    public static TodayFragment newInstance(long habitId) {
        TodayFragment fragment = new TodayFragment();
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
        initDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadTodayRecords();
        return view;
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        recordDao = new TriggerRecordDao(dbHelper);
        habitDao = new BadHabitDao(dbHelper);
    }

    private void initViews(View view) {
        statusCard = view.findViewById(R.id.status_card);
        tvCurrentCount = view.findViewById(R.id.tv_current_count);
        tvDailyLimit = view.findViewById(R.id.tv_daily_limit);
        tvStatus = view.findViewById(R.id.tv_status);
        tvNoRecords = view.findViewById(R.id.tv_no_records);
        rvRecords = view.findViewById(R.id.rv_records);
        fabAdd = view.findViewById(R.id.fab_add_record);
    }

    private void setupRecyclerView() {
        recordList = new ArrayList<>();
        adapter = new TriggerRecordAdapter(recordList, this);
        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecords.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddRecordDialog());
    }

    private void loadTodayRecords() {
        if (habitId == -1) return;

        recordList.clear();
        List<TriggerRecord> todayRecords = recordDao.getTodayRecords(habitId);
        recordList.addAll(todayRecords);
        adapter.notifyDataSetChanged();

        updateStatusDisplay();
        updateEmptyView();
    }

    private void updateStatusDisplay() {
        if (habitId == -1) return;

        BadHabit habit = habitDao.getHabitById(habitId);
        if (habit == null) return;

        int currentCount = recordList.size();
        int dailyLimit = habit.getDailyLimit();

        tvCurrentCount.setText(getString(R.string.current_count, currentCount));
        tvDailyLimit.setText(getString(R.string.daily_limit, dailyLimit));

        if (currentCount > dailyLimit) {
            tvStatus.setText(R.string.limit_exceeded);
            tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_exceeded));
            statusCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
        } else {
            tvStatus.setText(R.string.within_limit);
            tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_normal));
            statusCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
        }
    }

    private void updateEmptyView() {
        if (recordList.isEmpty()) {
            tvNoRecords.setVisibility(View.VISIBLE);
            rvRecords.setVisibility(View.GONE);
        } else {
            tvNoRecords.setVisibility(View.GONE);
            rvRecords.setVisibility(View.VISIBLE);
        }
    }

    private void showAddRecordDialog() {
        if (habitId == -1) {
            Toast.makeText(getContext(), R.string.error_no_habits, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_record, null);
        TextView tvTime = dialogView.findViewById(R.id.tv_time);
        EditText etDescription = dialogView.findViewById(R.id.et_description);

        // 设置当前时间
        String currentTime = DateUtils.getCurrentTimeString();
        tvTime.setText(currentTime);

        // 时间选择点击事件
        tvTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                        String time = DateUtils.buildTimeString(hourOfDay, minuteOfHour);
                        tvTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_record)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String time = tvTime.getText().toString();
                    String description = etDescription.getText().toString().trim();
                    saveNewRecord(time, description);
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.show();
    }

    private void saveNewRecord(String time, String description) {
        TriggerRecord record = new TriggerRecord(habitId, time, description);
        long recordId = recordDao.insertRecord(record);

        if (recordId > 0) {
            record.setId(recordId);
            recordList.add(record);
            adapter.notifyItemInserted(recordList.size() - 1);
            updateStatusDisplay();
            updateEmptyView();
            Toast.makeText(getContext(), R.string.record_saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditRecord(TriggerRecord record) {
        showEditRecordDialog(record);
    }

    @Override
    public void onDeleteRecord(TriggerRecord record) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_record)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    int deletedRows = recordDao.deleteRecord(record.getId());
                    if (deletedRows > 0) {
                        int position = recordList.indexOf(record);
                        if (position >= 0) {
                            recordList.remove(position);
                            adapter.notifyItemRemoved(position);
                            updateStatusDisplay();
                            updateEmptyView();
                        }
                        Toast.makeText(getContext(), R.string.record_deleted, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showEditRecordDialog(TriggerRecord record) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_record, null);
        TextView tvTime = dialogView.findViewById(R.id.tv_time);
        EditText etDescription = dialogView.findViewById(R.id.et_description);

        // 设置当前值
        tvTime.setText(record.getTriggerTime());
        etDescription.setText(record.getDescription());

        // 时间选择点击事件
        tvTime.setOnClickListener(v -> {
            // 解析当前时间
            String[] timeParts = record.getTriggerTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                        String time = DateUtils.buildTimeString(hourOfDay, minuteOfHour);
                        tvTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String time = tvTime.getText().toString();
                    String description = etDescription.getText().toString().trim();
                    updateRecord(record, time, description);
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.show();
    }

    private void updateRecord(TriggerRecord record, String time, String description) {
        record.setTriggerTime(time);
        record.setDescription(description);

        int updatedRows = recordDao.updateRecord(record);
        if (updatedRows > 0) {
            int position = recordList.indexOf(record);
            if (position >= 0) {
                adapter.notifyItemChanged(position);
            }
            Toast.makeText(getContext(), R.string.record_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当习惯发生变化时调用
     */
    public void onHabitChanged(long newHabitId) {
        this.habitId = newHabitId;
        loadTodayRecords();
    }
}