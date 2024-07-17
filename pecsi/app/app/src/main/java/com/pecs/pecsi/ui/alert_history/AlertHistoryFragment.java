package com.pecs.pecsi.ui.alert_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.AlertHistoryItem;
import com.pecs.pecsi.ui.policy_settings.PolicySettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AlertHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlertHistoryAdapter adapter;
    private List<AlertHistoryItem> alertList;

    public AlertHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alert_history, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_alert_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: Read from JSON responses
        // Initialize the alert list
        alertList = parseAlertHistoryFromJson();

        // Initialize the adapter
        adapter = new AlertHistoryAdapter(alertList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private List<AlertHistoryItem> parseAlertHistoryFromJson() {
        List<AlertHistoryItem> alertHistoryList = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.alert_history);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String alertType = jsonObject.getString("alertType");
                String timestamp = jsonObject.getString("timestamp");
                String details = jsonObject.getString("details");
                alertHistoryList.add(new AlertHistoryItem(alertType, timestamp, details));
            }
        } catch (IOException | JSONException e) {
            Log.e(getTag(), "Error parsing alert history JSON", e);
        }
        return alertHistoryList;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}