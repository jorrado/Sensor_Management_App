package com.amaris.sensorprocessor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Table("Gateways")
public class Gateway {

    public Gateway() {}

    public Gateway(String idGateway, String ipAddress, String commissioningDate,
//                   Boolean status,
                   String buildingName, Integer floor,String location) {
        this.idGateway = idGateway;
        this.ipAddress = ipAddress;
        this.commissioningDate = commissioningDate;
//        this.status = status;
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

//    @Column("status")
//    private Boolean status;

    @Column("building_name")
    private String buildingName;

    @Column("floor")
    private Integer floor;

    @Column("location")
    private String location;

//    @MappedCollection(idColumn = "id_gateway")
//    private List<Sensor> sensorList;

}
