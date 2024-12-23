package com.example.pecsbinder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class PECSBinderService extends Service {
    public PECSBinderService() {
    }

    private final IPECSBinderService.Stub binder = new IPECSBinderService.Stub() {
        @Override
        public String getUserPolicy() throws RemoteException {
            //Can replace with actual logic for retrieving policy
            String policy = "{\"preferences\":{\"alertType\":0,\"global\":{\"present\":true,\"preferences\":{\"read-calendar\":true,\"write-calendar\":false,\"camera\":false,\"read-contacts\":true,\"write-contacts\":false,\"fine-location\":false,\"coarse-location\":true,\"record-audio\":false,\"read-phone-state\":false,\"call-phone\":false,\"read-call-log\":true,\"write-call-log\":false,\"process-outgoing-calls\":false,\"send-sms\":true,\"receive-sms\":true,\"read-external-storage\":true,\"write-external-storage\":true,\"access-network-state\":true,\"access-wifi-state\":false,\"change-network-state\":true,\"change-wifi-state\":true,\"bluetooth\":true,\"bluetooth-admin\":false,\"wake-lock\":true,\"accessibility-service\":false,\"voice-interaction\":false}},\"engineData\":{\"fuel-system\":true,\"engine-load\":true,\"coolant-temp\":true,\"fuel-pressure\":true,\"maf-pressure\":true,\"rpm\":true,\"vehicle-speed\":true,\"timing-advance\":false,\"throttle-position\":true,\"o2-sensors\":false,\"run-time\":false,\"distance-with-mil\":false,\"fuel-rail-pressure\":false,\"egr-commander\":true,\"dtc-list\":false,\"fuel-tank-level\":false,\"total-distance\":true,\"air-fuel-ratio\":true,\"ambient-air-temp\":true,\"oil-temp\":true,\"exhaust-gas-temp\":false,\"nox-sensor\":false},\"appSpecific\":[{\"name\":\"app1\",\"preferences\":{\"read-calendar\":true,\"write-calendar\":false,\"camera\":false,\"read-contacts\":true,\"write-contacts\":false,\"fine-location\":false,\"coarse-location\":true,\"record-audio\":false,\"read-phone-state\":false,\"call-phone\":false,\"read-call-log\":true,\"write-call-log\":false,\"process-outgoing-calls\":false,\"send-sms\":true,\"receive-sms\":true,\"read-external-storage\":false,\"write-external-storage\":false,\"access-network-state\":true,\"access-wifi-state\":true,\"change-network-state\":true,\"change-wifi-state\":true,\"bluetooth\":true,\"bluetooth-admin\":false,\"wake-lock\":true,\"accessibility-service\":false,\"voice-interaction\":false}},{\"name\":\"app2\",\"preferences\":{\"read-calendar\":false,\"write-calendar\":false,\"camera\":false,\"read-contacts\":true,\"write-contacts\":false,\"fine-location\":false,\"coarse-location\":true,\"record-audio\":false,\"read-phone-state\":false,\"call-phone\":false,\"read-call-log\":true,\"write-call-log\":true,\"process-outgoing-calls\":false,\"send-sms\":true,\"receive-sms\":true,\"read-external-storage\":false,\"write-external-storage\":false,\"access-network-state\":false,\"access-wifi-state\":false,\"change-network-state\":true,\"change-wifi-state\":true,\"bluetooth\":false,\"bluetooth-admin\":false,\"wake-lock\":true,\"accessibility-service\":false,\"voice-interaction\":false}}]}}";
            return policy;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}