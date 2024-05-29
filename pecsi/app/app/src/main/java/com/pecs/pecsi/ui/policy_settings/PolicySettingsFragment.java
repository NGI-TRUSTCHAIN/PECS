package com.pecs.pecsi.ui.policy_settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.PolicySettingsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PolicySettingsFragment extends Fragment implements PolicySettingsAdapter.OnToggleChangeListener {

    private PolicySettingsViewModel policySettingsViewModel;
    private RecyclerView recyclerView;
    private PolicySettingsAdapter adapter;
    private List<PolicySettingsItem> policySettingsList;

    public PolicySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_policy_settings, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_policy_settings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the settings list
        policySettingsList = parsePolicySettingsFromJson();

        // Initialize the adapter
        adapter = new PolicySettingsAdapter(policySettingsList, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private List<PolicySettingsItem> parsePolicySettingsFromJson() {
        List<PolicySettingsItem> policySettingsList = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.policy_settings);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");
                Boolean isEnabled = jsonObject.getBoolean("isEnabled");
                policySettingsList.add(new PolicySettingsItem(name, description, isEnabled));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return policySettingsList;
    }

    @Override
    public void onToggleChanged(int position, boolean isEnabled) {
        PolicySettingsItem item = policySettingsList.get(position);
        item.setEnabled(item.isEnabled());
        sendPolicySettings();
    }

    private void sendPolicySettings() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (PolicySettingsItem item : policySettingsList) {
                JSONObject obj = new JSONObject();
                obj.put("settingName", item.getName());
                obj.put("settingDescription", item.getDescription());
                obj.put("isEnabled", item.isEnabled());
                jsonArray.put(obj);
            }
            JSONObject settings = new JSONObject();
            settings.put("policySettings", jsonArray);
            Log.i("OnToggleChanged", jsonArray.toString());
            // TODO: Call SDK method to send the settings
            // sendSettingsToComponent(settings);
        } catch (JSONException e) {
            Log.e("PolicySettingsFragment", "Error creating JSON", e);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
