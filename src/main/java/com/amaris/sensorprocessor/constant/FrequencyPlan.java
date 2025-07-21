package com.amaris.sensorprocessor.constant;

public enum FrequencyPlan {

    EUROPE("Europe 863-870 MHz (SF9 for RX2 - recommended)"),
    UNITED_STATES("United States 902-928 MHz, FSB 2 (used by TTN)"),
    AUSTRALIA("Australia 915-928 MHz, FSB 2 (used by TTN)"),
    CHINA("China 470-510 MHz, FSB 11 (used by TTN)"),
    ASIA("Asia 920-923 MHz (used by TTN Australia)");

    private final String description;

    FrequencyPlan(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
