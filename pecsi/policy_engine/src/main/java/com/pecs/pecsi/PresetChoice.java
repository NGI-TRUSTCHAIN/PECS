package com.pecs.pecsi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class to represent preset chosen by user
 */
@SuppressWarnings("unused")
public class PresetChoice {
    @JsonProperty("preset")
    private String choice;

    /**
     * 
     * @return String of preset name
     */
    public String getChoice() {return this.choice;}
}
