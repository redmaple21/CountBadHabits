package com.felix.countbadhabits.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.activity.MainActivity;
import com.felix.countbadhabits.adapter.HabitListAdapter;
import com.felix.countbadhabits.database.BadHabitDao;
import com.felix.countbadhabits.database.DatabaseHelper;
import com.felix.countbadhabits.model.BadHabit;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置Fragment
 */
public class SettingsFragment extends Fragment implements HabitListAdapter.OnHabitActionListener {
    private Button btnAddHabit;
    private RecyclerView rvHabits;
    
    private BadHabitDao habitDao;
    private HabitListAdapter adapter;
    private List<BadHabit> habitList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadHabitList();
        return view;
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        habitDao = new BadHabitDao(dbHelper);
    }

    private void initViews(View view) {
        btnAddHabit = view.findViewById(R.id.btn_add_habit);
        rvHabits = view.findViewById(R.id.rv_habits);
    }

    private void setupRecyclerView() {
        habitList = new ArrayList<>();
        adapter = new HabitListAdapter(habitList, this);
        rvHabits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHabits.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddHabit.setOnClickListener(v -> showAddHabitDialog());
    }

    private void loadHabitList() {
        habitList.clear();
        List<BadHabit> habits = habitDao.getAllActiveHabits();
        habitList.addAll(habits);
        adapter.notifyDataSetChanged();
    }

    private void showAddHabitDialog() {
        showHabitDialog(null, getString(R.string.add_new_habit));
    }

    private void showEditHabitDialog(BadHabit habit) {
        showHabitDialog(habit, getString(R.string.edit_habit));
    }

    private void showHabitDialog(BadHabit habit, String title) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_habit, null);
        EditText etHabitName = dialogView.findViewById(R.id.et_habit_name);
        EditText etDailyLimit = dialogView.findViewById(R.id.et_daily_limit);

        // 如果是编辑模式，填充现有数据
        if (habit != null) {
            etHabitName.setText(habit.getName());
            etDailyLimit.setText(String.valueOf(habit.getDailyLimit()));
        } else {
            etDailyLimit.setText("5"); // 默认上限
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null) // 设置为null，后面重写点击事件
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String name = etHabitName.getText().toString().trim();
                String limitStr = etDailyLimit.getText().toString().trim();

                if (validateInput(name, limitStr)) {
                    int dailyLimit = Integer.parseInt(limitStr);
                    
                    if (habit == null) {
                        // 添加新习惯
                        addNewHabit(name, dailyLimit);
                    } else {
                        // 更新现有习惯
                        updateHabit(habit, name, dailyLimit);
                    }
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(String name, String limitStr) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), R.string.habit_name_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int limit = Integer.parseInt(limitStr);
            if (limit <= 0) {
                Toast.makeText(getContext(), R.string.habit_limit_invalid, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.habit_limit_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addNewHabit(String name, int dailyLimit) {
        BadHabit newHabit = new BadHabit(name, dailyLimit);
        long habitId = habitDao.insertHabit(newHabit);

        if (habitId > 0) {
            newHabit.setId(habitId);
            habitList.add(newHabit);
            adapter.notifyItemInserted(habitList.size() - 1);
            
            // 刷新MainActivity的习惯列表
            refreshMainActivityHabitList();
            
            Toast.makeText(getContext(), R.string.habit_added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHabit(BadHabit habit, String name, int dailyLimit) {
        habit.setName(name);
        habit.setDailyLimit(dailyLimit);

        int updatedRows = habitDao.updateHabit(habit);
        if (updatedRows > 0) {
            int position = habitList.indexOf(habit);
            if (position >= 0) {
                adapter.notifyItemChanged(position);
            }
            
            // 刷新MainActivity的习惯列表
            refreshMainActivityHabitList();
            
            Toast.makeText(getContext(), R.string.habit_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditHabit(BadHabit habit) {
        showEditHabitDialog(habit);
    }

    @Override
    public void onDeleteHabit(BadHabit habit) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_habit)
                .setMessage(R.string.delete_habit_confirm)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // 使用永久删除，这样会级联删除所有相关记录
                    int deletedRows = habitDao.permanentDeleteHabit(habit.getId());
                    if (deletedRows > 0) {
                        int position = habitList.indexOf(habit);
                        if (position >= 0) {
                            habitList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        
                        // 刷新MainActivity的习惯列表
                        refreshMainActivityHabitList();
                        
                        Toast.makeText(getContext(), R.string.habit_deleted, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onToggleHabitStatus(BadHabit habit) {
        habit.setActive(!habit.isActive());
        int updatedRows = habitDao.updateHabit(habit);
        
        if (updatedRows > 0) {
            int position = habitList.indexOf(habit);
            if (position >= 0) {
                adapter.notifyItemChanged(position);
            }
            
            // 刷新MainActivity的习惯列表
            refreshMainActivityHabitList();
            
            String message = habit.isActive() ? getString(R.string.habit_enabled) : getString(R.string.habit_disabled);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            // 回滚状态
            habit.setActive(!habit.isActive());
            Toast.makeText(getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshMainActivityHabitList() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).refreshHabitList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 当从其他页面返回时刷新列表
        loadHabitList();
    }
}