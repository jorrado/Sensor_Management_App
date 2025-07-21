package com.amaris.sensorprocessor.entity;

import lombok.Data;

@Data
public class LorawanGatewayData {

    private GatewayPayload gateway;

    @Data
    public static class GatewayPayload {
        private Ids ids;
        private String frequency_plan_id;

        @Data
        public static class Ids {
            private String gateway_id;
            private String eui;
        }
    }

}
