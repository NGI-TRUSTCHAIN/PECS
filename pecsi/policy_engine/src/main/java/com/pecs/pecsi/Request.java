package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Class to represent data access request
 */
@SuppressWarnings("unused")
public class Request {
    @JsonProperty("data")
    private List<AppPermission> appPermissions;
    
    /**
     * Applications permissions class
     */
    public static class AppPermission {
        @JsonProperty("name")
        private String appName;

        @JsonProperty("permissions")
        private Map<String, Boolean> permissions;

        /**
         * 
         * @return Map of single application granted permissions
         */
        public Map<String, Boolean> getAppPermissions() {return this.permissions;}

        /**
         * 
         * @return The application name (package)
         */
        public String getAppName() {return this.appName;}
    }

    /**
     * Method to interact with instances of this class
     * @return List of AppPermission objects
     */
    public List<AppPermission> getPermissions() {return this.appPermissions;}
}
