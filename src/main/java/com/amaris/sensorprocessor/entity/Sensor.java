package com.amaris.sensorprocessor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("Sensors")
public class Sensor {

    public Sensor() {}

    public Sensor(String idSensor, String deviceType, LocalDateTime commissioningDate, Boolean status,
                  String batimentName, Integer etage, String emplacement, String idGateway) {
        this.idSensor = idSensor;
        this.deviceType = deviceType;
        this.commissioningDate = commissioningDate;
        this.status = status;
        this.batimentName = batimentName;
        this.etage = etage;
        this.emplacement = emplacement;
        this.idGateway = idGateway;
    }

    @Id
    @Column("id_sensor")
    private String idSensor;

    @Column("device_type")
    private String deviceType;

    @Column("commissioning_date")
    private LocalDateTime commissioningDate;

    @Column("status")
    private Boolean status;

    @Column("batiment_name")
    private String batimentName;

    @Column("etage")
    private Integer etage;

    @Column("emplacement")
    private String emplacement;

    @Column("id_gateway")
    private String idGateway;

}
