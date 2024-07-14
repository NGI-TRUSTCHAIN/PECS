package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Request {
    @JsonProperty("data")
    private List<AppPermission> appPermissions;
    
    public static class AppPermission {
        @JsonProperty("name")
        private String appName;

        @JsonProperty("permissions")
        private Map<String, Boolean> permissions;

        public Map<String, Boolean> getAppPermissions() {return this.permissions;}
        public String getAppName() {return this.appName;}
    }

    public List<AppPermission> getPermissions() {return this.appPermissions;}
}
