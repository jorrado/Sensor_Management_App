package com.amaris.sensorprocessor.entity;

import lombok.Data;

@Data
public class LorawanSensorData {

    private SensorPayload gateway;

    @Data
    public static class SensorPayload {
        private Ids ids;

        @Data
        public static class Ids {
            private String sensor_id;
        }
    }

}
