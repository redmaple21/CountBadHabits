package com.felix.countbadhabits.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.felix.countbadhabits.R;
import com.felix.countbadhabits.model.TriggerRecord;

import java.util.List;

/**
 * 触发记录列表适配器
 */
public class TriggerRecordAdapter extends RecyclerView.Adapter<TriggerRecordAdapter.ViewHolder> {
    private List<TriggerRecord> recordList;
    private OnRecordClickListener clickListener;

    public interface OnRecordClickListener {
        void onEditRecord(TriggerRecord record);
        void onDeleteRecord(TriggerRecord record);
    }

    public TriggerRecordAdapter(List<TriggerRecord> recordList, OnRecordClickListener clickListener) {
        this.recordList = recordList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trigger_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TriggerRecord record = recordList.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSequenceNumber;
        private TextView tvTime;
        private TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSequenceNumber = itemView.findViewById(R.id.tv_sequence_number);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDescription = itemView.findViewById(R.id.tv_description);

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onEditRecord(recordList.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onDeleteRecord(recordList.get(position));
                }
                return true;
            });
        }

        public void bind(TriggerRecord record) {
            tvSequenceNumber.setText(itemView.getContext().getString(R.string.sequence_number, record.getSequenceNumber()));
            tvTime.setText(record.getDisplayTime());
            tvDescription.setText(record.getDisplayDescription());
        }
    }
}