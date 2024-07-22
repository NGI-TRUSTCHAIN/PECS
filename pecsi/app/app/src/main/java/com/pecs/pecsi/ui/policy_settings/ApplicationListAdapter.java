package com.pecs.pecsi.ui.policy_settings;

import android.content.pm.ApplicationInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.services.PECSServiceSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ApplicationViewHolder> {

    private static final String TAG = PECSServiceSDK.class.getSimpleName();

    private List<JSONObject> applicationList;
    private final Set<String> selectedApplications;

    public ApplicationListAdapter(List<JSONObject> applicationList, Set<String> selectedApplications) {
        this.applicationList = applicationList;
        this.selectedApplications = selectedApplications;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        try {
            JSONObject app = applicationList.get(position);
            String appName = app.getString("app");
            String packageName = app.getString("package");

            holder.applicationName.setText(appName);
            holder.applicationPackage.setText(packageName);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedApplications.contains(packageName));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedApplications.add(packageName);
                } else {
                    selectedApplications.remove(packageName);
                }
            });

        }
        catch (JSONException e) {
            Log.e(TAG, "JSON error", e);
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView applicationName;
        TextView applicationPackage;
        CheckBox checkBox;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            applicationName = itemView.findViewById(R.id.application_name);
            applicationPackage = itemView.findViewById(R.id.application_package);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}