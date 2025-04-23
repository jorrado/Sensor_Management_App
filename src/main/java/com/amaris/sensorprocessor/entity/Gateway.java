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

    public Gateway(String idGateway, String commissioningDate,
                   Boolean status, String batimentName, Integer etage,
                   String emplacement) {
        this.idGateway = idGateway;
        this.commissioningDate = commissioningDate;
        this.status = status;
        this.batimentName = batimentName;
        this.etage = etage;
        this.emplacement = emplacement;
    }

    @Id
    @Column("id_gateway")
    private String idGateway;

    @Column("commissioning_date")
    private String commissioningDate;

    @Column("status")
    private Boolean status;

    @Column("batiment_name")
    private String batimentName;

    @Column("etage")
    private Integer etage;

    @Column("emplacement")
    private String emplacement;

    @MappedCollection(idColumn = "id_gateway")
    private List<Sensor> sensorList;

}
