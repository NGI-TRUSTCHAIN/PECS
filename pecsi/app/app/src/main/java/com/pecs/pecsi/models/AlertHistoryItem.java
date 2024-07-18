package com.pecs.pecsi.models;

public class AlertHistoryItem {
    private String appName;
    private String data;
    private long timestamp;
    private String date;

    public AlertHistoryItem(String appName, String data, long timestamp, String date) {
        this.appName = appName;
        this.data = data;
        this.timestamp = timestamp;
        this.date = date;
    }

    public String getAppName() {
        return appName;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }
}
