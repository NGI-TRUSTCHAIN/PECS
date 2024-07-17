package com.pecs.pecsi.ui.policy_settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.PolicySettingsItem;
import com.pecs.pecsi.services.PECSServiceSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PolicySettingsFragment extends Fragment implements PolicySettingsAdapter.OnToggleChangeListener {

    private Messenger serviceMessenger = null;
    private boolean isBound = false;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(getTag(), "Permission granted");
                } else {
                    Log.d(getTag(), "Permission not granted");
                }
            });

    private RecyclerView recyclerView;
    private PolicySettingsAdapter adapter;
    private List<PolicySettingsItem> policySettingsList;
    private List<PolicySettingsItem> policyEngineDataSettingsList;
    private List<PolicySettingsItem> combinedPolicySettingsList;

    private String selectedPreset;
    private String selectionType;
    private Set<String> selectedApplications;
    private JSONArray installedApplications;

    public PolicySettingsFragment() {
        // Required empty public constructor
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            serviceMessenger = new Messenger(iBinder);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceMessenger = null;
            isBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // PECSServiceSDK intent
        Intent intent = new Intent(getActivity(), PECSServiceSDK.class);
        isBound = getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(getTag(), "Service binding result: " + isBound);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_policy_settings, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_policy_settings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button saveButton = rootView.findViewById(R.id.button_save_settings);
        saveButton.setOnClickListener(v -> onSaveSettings());
        
        // Handle all preferences that the user set in the previous screens
        handlePreferences();

        // Get settings from correspondent JSON preset
        policySettingsList = new ArrayList<>();
        policyEngineDataSettingsList = new ArrayList<>();
        parsePolicySettingsFromJsonPreset();

        // Combine policySettingsList and policyEngineDataSettingsList for displaying them
        combinedPolicySettingsList = new ArrayList<>(policySettingsList);
        combinedPolicySettingsList.addAll(policyEngineDataSettingsList);
        // Initialize the adapter
        adapter = new PolicySettingsAdapter(combinedPolicySettingsList, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void handlePreferences() {
        // Retrieve selection type, selected preset and selected applications from SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        selectionType = sharedPref.getString("selectionType", "global");
        selectedPreset = sharedPref.getString("selectedPreset", "No Preset");
        String installedApplicationsString = sharedPref.getString("installedApplications", null);
        if (installedApplicationsString != null) {
            try {
                installedApplications = new JSONArray(installedApplicationsString);
            } catch (JSONException e) {
                Log.e(getTag(), "Error parsing JSON", e);
            }
        }
        selectedApplications = sharedPref.getStringSet("selectedApplications", null);
        Log.d(getTag(), "Shared preferences retrieved: selectionType: " + selectionType +
                " and selectedPreset: " + selectedPreset +
                " and selectedApplications: " + selectedApplications);
    }

    private void parsePolicySettingsFromJsonPreset() {
        int resId;
        switch (selectedPreset) {
            case "zeroShare":
                resId = R.raw.zero_share;
                break;
            case "veryLow":
                resId = R.raw.very_low;
                break;
            case "low":
                resId = R.raw.low;
                break;
            case "mid":
                resId = R.raw.mid;
                break;
            case "high":
                resId = R.raw.high;
                break;
            case "max":
                resId = R.raw.max;
                break;
            default:
                resId = R.raw.mid;
        }
        try {
            InputStream inputStream = getResources().openRawResource(resId);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject rootObject = new JSONObject(json);

            // Parse (global) preferences
            parseJSONPreferences(rootObject.getJSONObject("preferences").getJSONObject("global").getJSONArray("preferences"), "preferences");
            // Parse engine data preferences
            parseJSONPreferences(rootObject.getJSONObject("preferences").getJSONArray("engineData"), "engineData");


        } catch (IOException | JSONException e) {
            Log.e(getTag(), "Error parsing policy settings JSON", e);
        }
    }

    private void parseJSONPreferences(JSONArray preferencesArray, String preferencesType) throws JSONException {
        for (int i = 0; i < preferencesArray.length(); i++) {
            JSONObject jsonObject = preferencesArray.getJSONObject(i);
            String key = jsonObject.getString("key");
            String name = jsonObject.getString("name");
            String description = jsonObject.getString("description");
            Boolean isEnabled = jsonObject.getBoolean("isEnabled");
            PolicySettingsItem item = new PolicySettingsItem(key, name, description, isEnabled);
            if (preferencesType.equals("preferences")) {
                policySettingsList.add(item);
            } else {
                policyEngineDataSettingsList.add(item);
            }
        }
    }

    @Override
    public void onToggleChanged(int position, boolean isEnabled) {
        Log.d(getTag(), "Toggle state changed: " + isEnabled);
        PolicySettingsItem item = combinedPolicySettingsList.get(position);
        item.setEnabled(isEnabled);
        // User action overrides the preset
        selectedPreset = "No Preset";
    }

    private void sendPolicySettings() {
        if (!isBound) return;
        Log.d(getTag(), "Sending policy settings to PECSServiceSDK");

        JSONObject preferences = new JSONObject();
        JSONObject globalPreferences = new JSONObject();
        JSONArray engineDataPreferences = new JSONArray();
        JSONArray appSpecificPreferences = new JSONArray();

        JSONArray preferencesFlags = new JSONArray();
        try {
            // Get preferences flags
            for (PolicySettingsItem item : policySettingsList) {
                JSONObject obj = new JSONObject();
                obj.put(item.getKey(), item.isEnabled());
                preferencesFlags.put(obj);
            }
            // Get engineData flags
            for (PolicySettingsItem item : policyEngineDataSettingsList) {
                JSONObject obj = new JSONObject();
                obj.put(item.getKey(), item.isEnabled());
                engineDataPreferences.put(obj);
            }

            if (selectionType.equals("global")) {
                globalPreferences.put("present", true);
                globalPreferences.put("preferences", preferencesFlags);
            } else {
                globalPreferences.put("present", false);
                globalPreferences.put("preferences", new JSONArray());

                for (String app : selectedApplications) {
                    JSONObject appObj = new JSONObject();
                    appObj.put("name", app);    // Duplicate the array for each app
                    appObj.put("preferences", new JSONArray(preferencesFlags.toString()));
                    appSpecificPreferences.put(appObj);
                }
            }

            preferences.put("global", globalPreferences);
            preferences.put("engineData", engineDataPreferences);
            preferences.put("appSpecific", appSpecificPreferences);
            Log.d(getTag(), "Policy settings: " + preferences.toString());

            // Call SDK method to send the settings
            Message msg = Message.obtain(null, PECSServiceSDK.MSG_SEND_POLICY_SETTINGS);
            Bundle bundle = new Bundle();
            bundle.putString("jsonSettings", preferences.toString());
            msg.setData(bundle);
            serviceMessenger.send(msg);
        } catch (JSONException | RemoteException e) {
            Log.e(getTag(), "Error creating JSON", e);
        }
    }

    private void sendPolicyPreset() {
        Log.d(getTag(), "Sending policy preset to PECSServiceSDK");

        try {
            JSONObject choice = new JSONObject();
            choice.put("preset", selectedPreset);

            // Call SDK method to send the preset
            Message msg = Message.obtain(null, PECSServiceSDK.MSG_SEND_POLICY_CHOICE);
            Bundle bundle = new Bundle();
            bundle.putString("jsonChoice", choice.toString());
            msg.setData(bundle);
            serviceMessenger.send(msg);
        } catch (JSONException | RemoteException e) {
        Log.e(getTag(), "Error creating JSON", e);
        }
    }

    private void sendInstalledAppPermissionList() {
        Log.d(getTag(), "Sending installed app permission list to PECSServiceSDK");

        try {
            JSONObject permissions = new JSONObject();
            permissions.put("data", installedApplications);

            // Call SDK method to send the preset
            Message msg = Message.obtain(null, PECSServiceSDK.MSG_SEND_APP_PERMISSIONS);
            Bundle bundle = new Bundle();
            bundle.putString("jsonPermissions", permissions.toString());
            msg.setData(bundle);
            serviceMessenger.send(msg);
        } catch (JSONException | RemoteException e) {
            Log.e(getTag(), "Error creating JSON", e);
        }
    }

    private void onSaveSettings() {
        // Logic to handle saving settings
        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(getTag(), "ON SAVE SETTINGS: " + selectedPreset);
        if ("No Preset".equals(selectedPreset)) {
            sendPolicySettings();
        } else {
            sendPolicyPreset();
        }
        sendInstalledAppPermissionList();
        Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
