package com.amaris.sensorprocessor.constant;

import lombok.Getter;

@Getter
public enum FrequencyPlan {

    EUROPE("EU_863_870_TTN"),
    UNITED_STATES("US_902_928_FSB_2"),
    AUSTRALIA("AU_915_928_FSB_2"),
    CHINA("CN_470_510_FSB_11"),
    ASIA("AS_920_923");

    private final String description;

    FrequencyPlan(String description) {
        this.description = description;
    }

}
