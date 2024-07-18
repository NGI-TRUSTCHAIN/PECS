package com.pecs.pecsi.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;

public class PECSServiceSDK extends Service {
    private static final String TAG = PECSServiceSDK.class.getSimpleName();

    private static final String ROOT_FOLDER_PATH = Environment.getExternalStorageDirectory().toString();
    private static final String SETTINGS_FOLDER_PATH = ROOT_FOLDER_PATH + "/Download";
    private static final String RESPONSES_FOLDER_PATH = ROOT_FOLDER_PATH + "/Download";

    public static final int MSG_SEND_POLICY_SETTINGS = 1;
    public static final int MSG_SEND_POLICY_CHOICE = 2;

    private final Messenger messenger = new Messenger(new IncomingHandler(this));

    private static class IncomingHandler extends Handler {
        private final WeakReference<PECSServiceSDK> serviceReference;

        IncomingHandler(PECSServiceSDK service) {
            serviceReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            PECSServiceSDK service = serviceReference.get();
            if (service == null) {
                return;
            }

            switch (msg.what) {
                case MSG_SEND_POLICY_SETTINGS:
                    service.handleSendSettings(msg);
                    break;
                case MSG_SEND_POLICY_CHOICE:
                    service.handleSendChoice(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void handleSendSettings(Message msg) {
        String jsonSettings = msg.getData().getString("jsonSettings");
        Log.d(TAG, "Received settings: " + jsonSettings);

        String settingsPath = SETTINGS_FOLDER_PATH + "/preferences.json";
        saveJSONStringToFile(jsonSettings, settingsPath);
    }

    private void handleSendChoice(Message msg) {
        String jsonChoice = msg.getData().getString("jsonChoice");
        Log.d(TAG, "Received choice: " + jsonChoice);

        String choicePath = SETTINGS_FOLDER_PATH + "/choice.json";
        saveJSONStringToFile(jsonChoice, choicePath);
    }

    private void saveJSONStringToFile(String jsonString, String filePath) {
        File file = new File(filePath);
        BufferedWriter writer = null;

        try {
            // Write the JSON string to the file
            FileOutputStream fos = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(jsonString);
            Log.d(TAG, "Settings saved to file: " + filePath);
        } catch (Exception e) {
            Log.e(TAG, "Error saving settings to file", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing writer", e);
                }
            }
        }
    }

}
