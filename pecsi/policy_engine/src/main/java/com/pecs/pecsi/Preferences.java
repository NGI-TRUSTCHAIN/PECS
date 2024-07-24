package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.List;

@SuppressWarnings("unused")
public class Preferences {
    @JsonProperty("preferences")
    private UserPreferences preferences;

    public static class UserPreferences {
        @JsonProperty("global")
        private Global globals;

        @JsonProperty("engineData")
        private Map<String, Boolean> engineData;

        @JsonProperty("appSpecific")
        private List<AppSpecific> appSpecifics;

        @JsonProperty("alertType")
        private int alertType;

        public static class Global {
            @JsonProperty("present")
            private boolean present;

            @JsonProperty("preferences")
            private Map<String, Boolean> preferences;
    
            public boolean isPresent() {return this.present;}

            public Map<String, Boolean> getPrefs() {return this.preferences;}
        }
    
        public static class AppSpecific {
            @JsonProperty("name")
            private String appName;

            @JsonProperty("preferences")
            private Map<String, Boolean> preferences;

            public String getAppName() {return this.appName;}

            public Map<String, Boolean> getPrefs() {return this.preferences;}
        }

        public Global getGlobals() {return this.globals;}
        public Map<String, Boolean> getEngineDataPrefs() {return this.engineData;}
        public List<AppSpecific> getAppSpecificPrefs() {return this.appSpecifics;}
        public int getAlertType() {return this.alertType;}
    }


    public UserPreferences getPreferences() {return this.preferences;}
}
