package com.felix.countbadhabits.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.model.BadHabit;

import java.util.List;

/**
 * 习惯列表适配器
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> {
    private List<BadHabit> habitList;
    private OnHabitActionListener actionListener;

    public interface OnHabitActionListener {
        void onEditHabit(BadHabit habit);
        void onDeleteHabit(BadHabit habit);
        void onToggleHabitStatus(BadHabit habit);
    }

    public HabitListAdapter(List<BadHabit> habitList, OnHabitActionListener actionListener) {
        this.habitList = habitList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BadHabit habit = habitList.get(position);
        holder.bind(habit);
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHabitName;
        private TextView tvDailyLimit;
        private TextView tvCreatedDate;
        private Switch switchActive;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHabitName = itemView.findViewById(R.id.tv_habit_name);
            tvDailyLimit = itemView.findViewById(R.id.tv_daily_limit);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // 设置点击事件
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onEditHabit(habitList.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onDeleteHabit(habitList.get(position));
                }
            });

            switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    BadHabit habit = habitList.get(position);
                    if (habit.isActive() != isChecked) {
                        actionListener.onToggleHabitStatus(habit);
                    }
                }
            });
        }

        public void bind(BadHabit habit) {
            tvHabitName.setText(habit.getName());
            tvDailyLimit.setText(itemView.getContext().getString(R.string.daily_limit, habit.getDailyLimit()));
            tvCreatedDate.setText(itemView.getContext().getString(R.string.created_on, habit.getCreatedDate()));

            // 设置开关状态（需要先移除监听器再设置，避免触发回调）
            switchActive.setOnCheckedChangeListener(null);
            switchActive.setChecked(habit.isActive());
            switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    if (habit.isActive() != isChecked) {
                        actionListener.onToggleHabitStatus(habit);
                    }
                }
            });

            // 根据状态设置样式
            if (habit.isActive()) {
                tvHabitName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
                tvDailyLimit.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                itemView.setAlpha(1.0f);
            } else {
                tvHabitName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                tvDailyLimit.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                itemView.setAlpha(0.6f);
            }
        }
    }
}