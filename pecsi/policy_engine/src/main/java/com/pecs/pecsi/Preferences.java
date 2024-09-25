package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.List;

/**
 * Class to systematically represent user preferences
 */
@SuppressWarnings("unused")
public class Preferences {
    @JsonProperty("preferences")
    private UserPreferences preferences;

    /**
     * User preferences class
     */
    public static class UserPreferences {
        @JsonProperty("global")
        private Global globals;

        @JsonProperty("engineData")
        private Map<String, Boolean> engineData;

        @JsonProperty("appSpecific")
        private List<AppSpecific> appSpecifics;

        @JsonProperty("alertType")
        private int alertType;

        /**
         * System-global preferences
         */
        public static class Global {
            @JsonProperty("present")
            private boolean present;

            @JsonProperty("preferences")
            private Map<String, Boolean> preferences;
    
            public boolean isPresent() {return this.present;}

            public Map<String, Boolean> getPrefs() {return this.preferences;}
        }
    
        /**
         * App-specific preferences
         */
        public static class AppSpecific {
            @JsonProperty("name")
            private String appName;

            @JsonProperty("preferences")
            private Map<String, Boolean> preferences;

            public String getAppName() {return this.appName;}

            public Map<String, Boolean> getPrefs() {return this.preferences;}
        }

        /**
         * 
         * @return Global preferences object
         */
        public Global getGlobals() {return this.globals;}

        /**
         * 
         * @return Engine data preferences Map
         */
        public Map<String, Boolean> getEngineDataPrefs() {return this.engineData;}

        /**
         * 
         * @return List of application specific preferences (AppSpecific class)
         */
        public List<AppSpecific> getAppSpecificPrefs() {return this.appSpecifics;}

        /**
         * 
         * @return Integer representation of alert type set
         */
        public int getAlertType() {return this.alertType;}
    }

    /**
     * Entry point method to interact with instances of this class
     * @return UserPreferences object
     */
    public UserPreferences getPreferences() {return this.preferences;}
}
