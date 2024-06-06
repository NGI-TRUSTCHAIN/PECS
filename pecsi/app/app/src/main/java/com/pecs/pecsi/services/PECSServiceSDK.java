package com.pecs.pecsi.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;

public class PECSServiceSDK extends Service {
    private static final String TAG = PECSServiceSDK.class.getSimpleName();

    public static final int MSG_SEND_POLICY_SETTINGS = 1;

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
    }

}
