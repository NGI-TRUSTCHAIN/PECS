package com.pecs.pecsi.ui.policy_settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.PolicySettingsItem;

import java.util.ArrayList;
import java.util.List;

public class PolicySettingsAdapter extends RecyclerView.Adapter<PolicySettingsAdapter.ViewHolder> {

    private List<PolicySettingsItem> policySettingsList;
    private OnToggleChangeListener onToggleChangeListener;

    public PolicySettingsAdapter(List<PolicySettingsItem> policySettingsList, OnToggleChangeListener onToggleChangeListener) {
        this.policySettingsList = policySettingsList;
        this.onToggleChangeListener = onToggleChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_policy_settings, parent, false);
        return new ViewHolder(view);
    }

    public interface OnToggleChangeListener {
        void onToggleChanged(int position, boolean isEnabled);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PolicySettingsItem item = policySettingsList.get(position);
        holder.settingName.setText(item.getName());
        holder.settingDescription.setText(item.getDescription());
        holder.settingSwitch.setChecked(item.isEnabled());
        holder.settingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setEnabled(isChecked);
            onToggleChangeListener.onToggleChanged(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return policySettingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView settingName, settingDescription;
        Switch settingSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            settingName = itemView.findViewById(R.id.setting_name);
            settingDescription = itemView.findViewById(R.id.setting_description);
            settingSwitch = itemView.findViewById(R.id.setting_switch);
        }
    }
}
