package com.pecs.pecsi.models;

public class AlertHistoryItem {
    private String alertType;
    private String timestamp;
    private String details;

    public AlertHistoryItem(String alertType, String timestamp, String details) {
        this.alertType = alertType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }
}