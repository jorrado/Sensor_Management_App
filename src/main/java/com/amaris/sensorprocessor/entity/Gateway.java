package com.amaris.sensorprocessor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("Gateways")
public class Gateway {

    public Gateway() {}

    public Gateway(String idGateway, String ipAddress, String commissioningDate,
                   String buildingName, Integer floor, String location) {
        this.idGateway = idGateway;
        this.ipAddress = ipAddress;
        this.commissioningDate = commissioningDate;
        this.buildingName = buildingName;
        this.floor = floor;
        this.location = location;
    }

    @Id
    @Column("id_gateway")
    private String idGateway;

    @Column("ip_address")
    private String ipAddress;

    @Column("commissioning_date")
    private String commissioningDate;

    @Column("building_name")
    private String buildingName;

    @Column("floor")
    private Integer floor;

    @Column("location")
    private String location;

}
