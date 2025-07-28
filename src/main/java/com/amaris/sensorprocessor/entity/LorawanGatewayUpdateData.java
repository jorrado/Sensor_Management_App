package com.amaris.sensorprocessor.entity;

import lombok.Data;

@Data
public class LorawanGatewayUpdateData {

    private GatewayPayload gateway;
    private String field_mask;

    @Data
    public static class GatewayPayload {
        private Ids ids;
        private String[] frequency_plan_ids;

        @Data
        public static class Ids {
            private String gateway_id;
        }
    }

}
