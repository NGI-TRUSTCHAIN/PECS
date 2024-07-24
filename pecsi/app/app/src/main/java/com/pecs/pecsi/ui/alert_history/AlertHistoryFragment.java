package com.pecs.pecsi.ui.alert_history;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.AlertHistoryItem;
import com.pecs.pecsi.ui.policy_settings.PolicySettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
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
        TextView noAlertsTextView = rootView.findViewById(R.id.no_alerts_text);
        recyclerView = rootView.findViewById(R.id.recycler_view_alert_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the alert list
        alertList = parseAlertHistoryFromJson();

        // Initialize the adapter
        adapter = new AlertHistoryAdapter(getContext(), alertList);

        if (alertList.isEmpty()) {
            noAlertsTextView.setText("All good, there have been no alerts so far!");
            noAlertsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noAlertsTextView.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private List<AlertHistoryItem> parseAlertHistoryFromJson() {
        List<AlertHistoryItem> alertHistoryList = new ArrayList<>();
        String filePath = Environment.getExternalStorageDirectory().toString() + "/Download/response.json";
        File file = new File(filePath);

        if (!file.exists()) {
            Log.d(getTag(), "JSON file does not exist");
            return alertHistoryList;
        }

        try {
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray responsesArray = jsonObject.getJSONArray("responses");

            for (int i = 0; i < responsesArray.length(); i++) {
                JSONObject responseObject = responsesArray.getJSONObject(i);
                String appName = responseObject.getString("name");
                JSONArray alertsArray = responseObject.getJSONArray("alerts");

                for (int j = 0; j < alertsArray.length(); j++) {
                    JSONObject alertObject = alertsArray.getJSONObject(j);
                    String data = alertObject.getString("data");
                    long timestamp = alertObject.getLong("timestamp");            
                    String date = alertObject.getString("date");

                    AlertHistoryItem alertHistoryItem = new AlertHistoryItem(appName, data, timestamp, date);
                    alertHistoryList.add(alertHistoryItem);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(getTag(), "Error reading or parsing the JSON file", e);
        }

        return alertHistoryList;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}