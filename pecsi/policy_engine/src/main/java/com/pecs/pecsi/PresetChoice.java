package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class PresetChoice {
    @JsonProperty("preset")
    private String choice;

    public String getChoice() {return this.choice;}
}
