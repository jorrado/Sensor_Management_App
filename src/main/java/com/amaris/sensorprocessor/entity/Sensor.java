package com.amaris.sensorprocessor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("Sensors")
public class Sensor {

    public Sensor() {}

    public Sensor(String idSensor, String deviceType, String commissioningDate,
                  Boolean status, String buildingName, Integer floor,
                  String location, String idGateway) {
        this.idSensor = idSensor;
        this.deviceType = deviceType;
        this.commissioningDate = commissioningDate;
        this.status = status;
        this.buildingName = buildingName;
        this.floor = floor;
        this.location = location;
        this.idGateway = idGateway;
    }

    @Id
    @Column("id_sensor")
    private String idSensor;

    @Column("device_type")
    private String deviceType;

    @Column("commissioning_date")
    private String commissioningDate;

    @Column("status")
    private Boolean status;

    @Column("building_name")
    private String buildingName;

    @Column("floor")
    private Integer floor;

    @Column("location")
    private String location;

    @Column("id_gateway")
    private String idGateway;

}
