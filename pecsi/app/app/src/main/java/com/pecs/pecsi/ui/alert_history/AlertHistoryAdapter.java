package com.pecs.pecsi.ui.alert_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.AlertHistoryItem;

import java.util.List;

public class AlertHistoryAdapter extends RecyclerView.Adapter<AlertHistoryAdapter.ViewHolder> {

    private List<AlertHistoryItem> alertHistoryList;

    public AlertHistoryAdapter(List<AlertHistoryItem> alertHistoryList) {
        this.alertHistoryList = alertHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertHistoryItem alertHistoryItem = alertHistoryList.get(position);
        holder.appNameTextView.setText(alertHistoryItem.getAppName());
        holder.dataTextView.setText(alertHistoryItem.getData());
        holder.timestampTextView.setText(String.valueOf(alertHistoryItem.getTimestamp()));
        holder.dateTextView.setText(alertHistoryItem.getDate());
    }

    @Override
    public int getItemCount() {
        return alertHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView;
        TextView dataTextView;
        TextView timestampTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.app_name);
            dataTextView = itemView.findViewById(R.id.data);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            dateTextView = itemView.findViewById(R.id.date);
        }
    }
}
