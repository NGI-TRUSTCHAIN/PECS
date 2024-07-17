package com.pecs.pecsi.models;

import java.util.HashMap;
import java.util.Map;

public class PermissionData {
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

    public static final Map<String, String> permissionMapping = new HashMap<>();

    static {
        permissionMapping.put("android.permission.READ_CALENDAR", READ_CALENDAR);
        permissionMapping.put("android.permission.WRITE_CALENDAR", WRITE_CALENDAR);
        permissionMapping.put("android.permission.CAMERA", CAMERA);
        permissionMapping.put("android.permission.READ_CONTACTS", READ_CONTACTS);
        permissionMapping.put("android.permission.WRITE_CONTACTS", WRITE_CONTACTS);
        permissionMapping.put("android.permission.ACCESS_FINE_LOCATION", FINE_LOCATION);
        permissionMapping.put("android.permission.ACCESS_COARSE_LOCATION", COARSE_LOCATION);
        permissionMapping.put("android.permission.RECORD_AUDIO", RECORD_AUDIO);
        permissionMapping.put("android.permission.READ_PHONE_STATE", READ_PHONE_STATE);
        permissionMapping.put("android.permission.CALL_PHONE", CALL_PHONE);
        permissionMapping.put("android.permission.READ_CALL_LOG", READ_CALL_LOG);
        permissionMapping.put("android.permission.WRITE_CALL_LOG", WRITE_CALL_LOG);
        permissionMapping.put("android.permission.PROCESS_OUTGOING_CALLS", PROCESS_OUTGOING_CALLS);
        permissionMapping.put("android.permission.SEND_SMS", SEND_SMS);
        permissionMapping.put("android.permission.RECEIVE_SMS", RECEIVE_SMS);
        permissionMapping.put("android.permission.READ_EXTERNAL_STORAGE", READ_EXTERNAL_STORAGE);
        permissionMapping.put("android.permission.WRITE_EXTERNAL_STORAGE", WRITE_EXTERNAL_STORAGE);
        permissionMapping.put("android.permission.ACCESS_NETWORK_STATE", ACCESS_NETWORK_STATE);
        permissionMapping.put("android.permission.ACCESS_WIFI_STATE", ACCESS_WIFI_STATE);
        permissionMapping.put("android.permission.CHANGE_NETWORK_STATE", CHANGE_NETWORK_STATE);
        permissionMapping.put("android.permission.CHANGE_WIFI_STATE", CHANGE_WIFI_STATE);
        permissionMapping.put("android.permission.BLUETOOTH", BLUETOOTH);
        permissionMapping.put("android.permission.BLUETOOTH_ADMIN", BLUETOOTH_ADMIN);
        permissionMapping.put("android.permission.WAKE_LOCK", WAKE_LOCK);
        permissionMapping.put("android.permission.BIND_ACCESSIBILITY_SERVICE", ACCESSIBILITY_SERVICE);
        permissionMapping.put("android.permission.BIND_VOICE_INTERACTION", VOICE_INTERACTION);
    }
}