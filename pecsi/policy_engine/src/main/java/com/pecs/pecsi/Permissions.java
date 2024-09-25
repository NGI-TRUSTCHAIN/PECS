package com.pecs.pecsi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to represent possible permissions that user can give
 */
public class Permissions {

    /**
     * Engine data specific permissions class
     */
    public static class EngineData {
        public static final String FUEL_SYSTEM = "fuel-system";
        public static final String ENGINE_LOAD = "engine-load";
        public static final String COOLANT_TEMP = "coolant-temp";
        public static final String FUEL_PRESSURE = "fuel-pressure";
        public static final String MAF_PRESSURE = "maf-pressure";
        public static final String RPM = "rpm";
        public static final String VEHICLE_SPEED = "vehicle-speed";
        public static final String TIMING_ADVANCE = "timing-advance";
        public static final String THROTTLE_POSITION = "throttle-position";
        public static final String O2_SENSORS = "o2-sensors";
        public static final String RUN_TIME = "run-time";
        public static final String DISTANCE_WITH_MIL = "distance-with-mil";
        public static final String FUEL_RAIL_PRESSURE = "fuel-rail-pressure";
        public static final String EGR_COMMANDER = "egr-commander";
        public static final String DTC_LIST = "dtc-list";
        public static final String FUEL_TANK_LEVEL = "fuel-tank-level";
        public static final String TOTAL_DISTANCE = "total-distance";
        public static final String AIR_FUEL_RATIO = "air-fuel-ratio";
        public static final String AMBIENT_AIR_TEMP = "ambient-air-temp";
        public static final String OIL_TEMP = "oil-temp";
        public static final String EXHAUST_GAS_TEMP = "exhaust-gas-temp";
        public static final String NOX_SENSOR = "nox-sensor";
    }

    /**
     * Android permissions specific permissions class
     */
    public static class Data {
        public static final String READ_CALENDAR = "read-calendar";
        public static final String WRITE_CALENDAR = "write-calendar";
        public static final String CAMERA = "camera";
        public static final String READ_CONTACTS = "read-contacts";
        public static final String WRITE_CONTACTS = "write-contacts";
        public static final String FINE_LOCATION = "fine-location";
        public static final String COARSE_LOCATION = "coarse-location";
        public static final String RECORD_AUDIO = "record-audio";
        public static final String READ_PHONE_STATE = "read-phone-state";
        public static final String CALL_PHONE = "call-phone";
        public static final String READ_CALL_LOG = "read-call-log";
        public static final String WRITE_CALL_LOG = "write-call-log";
        public static final String PROCESS_OUTGOING_CALLS = "process-outgoing-calls";
        public static final String SEND_SMS = "send-sms";
        public static final String RECEIVE_SMS = "receive-sms";
        public static final String READ_EXTERNAL_STORAGE = "read-external-storage";
        public static final String WRITE_EXTERNAL_STORAGE = "write-external-storage";
        public static final String ACCESS_NETWORK_STATE = "access-network-state";
        public static final String ACCESS_WIFI_STATE = "access-wifi-state";
        public static final String CHANGE_NETWORK_STATE = "change-network-state";
        public static final String CHANGE_WIFI_STATE = "change-wifi-state";
        public static final String BLUETOOTH = "bluetooth";
        public static final String BLUETOOTH_ADMIN = "bluetooth-admin";
        public static final String WAKE_LOCK = "wake-lock";
        public static final String ACCESSIBILITY_SERVICE = "accessibility-service";
        public static final String VOICE_INTERACTION = "voice-interaction";
    }

    /**
     * Get the class constants
     * @param c Class name
     * @return List of class constants
     */
    private static List<String> getConsts(Class<?> c) {
        List<String> consts = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            try {consts.add((String) field.get(null));}
            catch (IllegalAccessException e) {e.printStackTrace();}
        }

        return consts;
    }

    /**
     * Get the list of engine data
     * @return String list of engine data
     */
    public static List<String> getEngineDataList() {
        return getConsts(EngineData.class);
    }

    /**
     * Get the list of Android permissions
     * @return String list of Android permissions
     */
    public static List<String> getDataList() {
        return getConsts(Data.class);
    }
}
