package com.amaris.sensorprocessor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LorawanSensorUpdateData {

    @JsonProperty("end_device")
    private EndDevice endDevice;

    @JsonProperty("field_mask")
    private FieldMask fieldMask;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EndDevice {
        private String name;                     // ex. champ que tu veux modifier
        private Map<String,String> attributes;   // ex. si tu veux pousser des tags
    }

    @Data
    public static class FieldMask {
        private List<String> paths; // ex: ["name","attributes"]
    }
}
