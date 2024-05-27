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

    private List<AlertHistoryItem> alertList;

    public AlertHistoryAdapter(List<AlertHistoryItem> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertHistoryItem item = alertList.get(position);
        holder.alertType.setText(item.getAlertType());
        holder.timestamp.setText(item.getTimestamp());
        holder.details.setText(item.getDetails());
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView alertType, timestamp, details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alertType = itemView.findViewById(R.id.alert_type);
            timestamp = itemView.findViewById(R.id.alert_timestamp);
            details = itemView.findViewById(R.id.alert_details);
        }
    }
}
