package com.pecs.pecsi;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class PolicyList {
    @JsonProperty("policies")
    List<Policy> policies;
    
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

        public String getUuid() {return this.uuid;}
        public String getFilePath() {return this.file;}
        public String getJsonPath() {return this.json;}
        public long getTimestamp() {return this.timestamp;}
        public String getDate() {return this.date;}
        public boolean isEnforced() {return this.enforced;}
        public boolean isFromPreset() {return this.fromPreset;}

        public void setEnforced(boolean enforced) {this.enforced = enforced;}
    }

    public List<Policy> getPolicies() {return this.policies;}
}
