package com.pecs.pecsi.ui.policy_settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pecs.pecsi.R;
import com.pecs.pecsi.models.PolicySettingsItem;
import com.pecs.pecsi.services.PECSServiceSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PolicySettingsFragment extends Fragment implements PolicySettingsAdapter.OnToggleChangeListener {
    private static final String TAG = PolicySettingsFragment.class.getSimpleName();

    private Messenger serviceMessenger = null;
    private boolean isBound = false;

    private RecyclerView recyclerView;
    private PolicySettingsAdapter adapter;
    private List<PolicySettingsItem> policySettingsList;

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
        Log.d(TAG, "Service binding result: " + isBound);
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
            Log.e(TAG, "Error parsing policy settings JSON", e);
        }
        return policySettingsList;
    }

    @Override
    public void onToggleChanged(int position, boolean isEnabled) {
        Log.d(TAG, "Toggle state changed: " + isEnabled);
        PolicySettingsItem item = policySettingsList.get(position);
        item.setEnabled(isEnabled);
        sendPolicySettings();
    }

    private void sendPolicySettings() {
        if (!isBound) return;
        Log.d(TAG, "Sending policy settings to PECSServiceSDK");

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
            Log.d(TAG, "Policy settings array: " + jsonArray.toString());

            // Call SDK method to send the settings
            Message msg = Message.obtain(null, PECSServiceSDK.MSG_SEND_POLICY_SETTINGS);
            Bundle bundle = new Bundle();
            bundle.putString("jsonSettings", settings.toString());
            msg.setData(bundle);
            serviceMessenger.send(msg);
        } catch (JSONException | RemoteException e) {
            Log.e(TAG, "Error creating JSON", e);
        }
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
