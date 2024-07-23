package com.pecs.pecsi.ui.alert_history;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
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
    private Context context;

    public AlertHistoryAdapter(Context context, List<AlertHistoryItem> alertHistoryList) {
        this.context = context;
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

        // Parse app name from package
        String packageName = alertHistoryItem.getAppName();
        PackageManager packageManager = context.getPackageManager();
        String appName = packageName;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.appNameTextView.setText(appName);

        holder.dataTextView.setText(alertHistoryItem.getData());
        holder.timestampTextView.setText(String.valueOf(alertHistoryItem.getTimestamp()));
        holder.dateTextView.setText(alertHistoryItem.getDate());

        // Set click listener for the settings link
        holder.settingsLinkTextView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            context.startActivity(intent);
        });
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
        TextView settingsLinkTextView; // Add this line

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.app_name);
            dataTextView = itemView.findViewById(R.id.data);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            dateTextView = itemView.findViewById(R.id.date);
            settingsLinkTextView = itemView.findViewById(R.id.settings_link);
        }
    }
}
