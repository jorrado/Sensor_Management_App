package com.amaris.sensorprocessor.entity;

import lombok.Data;

@Data
public class LorawanSensorUpdateData {

    private SensorPayload sensor;
    private String field_mask;

    @Data
    public static class SensorPayload {
        private Ids ids;

        @Data
        public static class Ids {
            private String sensor_id;
        }
    }

}
