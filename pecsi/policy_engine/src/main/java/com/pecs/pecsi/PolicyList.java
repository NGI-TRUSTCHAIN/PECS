package com.pecs.pecsi;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object class to represent list of policies
 */
@SuppressWarnings("unused")
public class PolicyList {
    @JsonProperty("policies")
    List<Policy> policies;
    
    /**
     * Single policy object class
     */
    public static class Policy {
        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("file")
        private String file;

        @JsonProperty("json")
        private String json;

        @JsonProperty("timestamp")
        private long timestamp;

        @JsonProperty("date")
        private String date;

        @JsonProperty("enforced")
        private boolean enforced;

        @JsonProperty("from-preset")
        private boolean fromPreset;

        /**
         * Get UUIDv4 assigned to the policy
         * @return UUIDv4 string
         */
        public String getUuid() {return this.uuid;}

        /**
         * 
         * @return Policy XML file path
         */
        public String getFilePath() {return this.file;}

        /**
         * 
         * @return Policy JSON file path
         */
        public String getJsonPath() {return this.json;}

        /**
         * 
         * @return UNIX timestamp of policy creation
         */
        public long getTimestamp() {return this.timestamp;}

        /**
         * 
         * @return Date string of privacy creation
         */
        public String getDate() {return this.date;}

        /**
         * 
         * @return true if the policy is enforced in the system, false otherwise
         */
        public boolean isEnforced() {return this.enforced;}

        /**
         * 
         * @return true if the policy is generated from a preset, false otherwise
         */
        public boolean isFromPreset() {return this.fromPreset;}

        /**
         * Setter to modify the enforcement of the policy
         * @param enforced Boolean value representing the desired enforcement
         */
        public void setEnforced(boolean enforced) {this.enforced = enforced;}
    }

    /**
     * Get list of policies
     * @return List of Policy objects
     */
    public List<Policy> getPolicies() {return this.policies;}
}
