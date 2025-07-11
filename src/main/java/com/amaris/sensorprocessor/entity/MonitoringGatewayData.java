package com.amaris.sensorprocessor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitoringGatewayData {

    private LocalDateTime timestamp;

    @JsonProperty("system")
    private SystemInfo system;

    @JsonProperty("ttn")
    private TtnInfo ttn;

    @Data
    public static class SystemInfo {
        private String hostname;

        @JsonProperty("ip_local")
        private String ipLocal;

        @JsonProperty("ip_public")
        private String ipPublic;

        @JsonProperty("cpu_percent (%)")
        private double cpuPercent;

        @JsonProperty("cpu_temp (C)")
        private double cpuTemp;

        @JsonProperty("ram_total_gb (GB)")
        private double ramTotalGb;

        @JsonProperty("ram_used_gb (GB)")
        private double ramUsedGb;

        @JsonProperty("disk_total")
        private String diskTotal;

        @JsonProperty("disk_used")
        private String diskUsed;

        @JsonProperty("disk_available")
        private String diskAvailable;

        @JsonProperty("disk_usage_percent")
        private String diskUsagePercent;

        @JsonProperty("uptime_days")
        private String uptimeDays;
    }

    @Data
    public static class TtnInfo {
        private Info info;

        @Data
        public static class Info {
            @JsonProperty("created_at")
            private LocalDateTime createdAt;
        }
    }
}
