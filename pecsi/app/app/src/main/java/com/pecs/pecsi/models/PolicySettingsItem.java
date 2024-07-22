package com.pecs.pecsi.models;

import com.pecs.pecsi.ui.policy_settings.PolicySettingsAdapter;

public class PolicySettingsItem {
    private String key, name, description, category;
    private boolean isEnabled, isHeader;

    public PolicySettingsItem(String key, String name, String description, boolean isEnabled, String category) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.isEnabled = isEnabled;
        this.category = category;
        this.isHeader = false;
    }

    public PolicySettingsItem(String name) {
        this.name = name;
        this.isHeader = true;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isHeader() { return isHeader; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
