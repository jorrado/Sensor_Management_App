package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GatewayDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GatewayDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Récupère la liste de toutes les passerelles enregistrées dans la base de données.
     *
     * @return une liste d'objets {@code Gateway} représentant toutes les passerelles.
     */
    public List<Gateway> findAllGateways() {
        return jdbcTemplate.query(
                "SELECT * FROM GATEWAYS ORDER BY LOWER(ID_GATEWAY) ASC;",
                new BeanPropertyRowMapper<Gateway>(Gateway.class));
    }

    /**
     * Récupère une passerelle spécifique en fonction de son identifiant.
     *
     * @param idGateway l'identifiant de la passerelle à rechercher.
     * @return un objet {@code Gateway} correspondant à l'ID fourni.
     */
    public Optional<Gateway> findByIdOfGateway(String idGateway) {
        List<Gateway> gateways = jdbcTemplate.query(
                "SELECT * FROM GATEWAYS WHERE ID_GATEWAY=?",
                new BeanPropertyRowMapper<>(Gateway.class),
                idGateway);

        return gateways.isEmpty() ? Optional.empty() : Optional.of(gateways.get(0));
    }

    /**
     * Supprime une passerelle de la base de données en fonction de son identifiant.
     *
     * @param idGateway l'identifiant de la passerelle à supprimer
     * @return le nombre de lignes affectées par la suppression (généralement 0 ou 1)
     */
    public int deleteByIdOfGateway(String idGateway) {
        return jdbcTemplate.update(
                "DELETE FROM GATEWAYS WHERE ID_GATEWAY=?",
                idGateway);
    }

    /**
     * Insère une nouvelle passerelle dans la base de données.
     *
     * @param gateway l'objet {@link Gateway} contenant les informations de la gateway à insérer.
     * @return le nombre de lignes affectées dans la base de données (1 si l'insertion a réussi, 0 sinon).
     */
    public int insertGateway(Gateway gateway) {
        return jdbcTemplate.update(
                "INSERT INTO GATEWAYS (" +
                        "ID_GATEWAY, IP_ADDRESS, COMMISSIONING_DATE, STATUS, " +
                        "BUILDING_NAME, FLOOR, LOCATION) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)",
                gateway.getIdGateway(), gateway.getIpAddress(), gateway.getCommissioningDate(), gateway.getStatus(),
                gateway.getBuildingName(), gateway.getFloor(), gateway.getLocation()
        );
    }

    /**
     * Met à jour les informations d'une passerelle dans la base de données.
     *
     * @param gateway l'objet {@link Gateway} contenant les nouvelles valeurs à mettre à jour.
     * @return le nombre de lignes affectées par la mise à jour (1 si l'id existe, 0 sinon).
     */
    public int updateGateway(Gateway gateway) {
        return jdbcTemplate.update(
                "UPDATE GATEWAYS SET " +
                        "IP_ADDRESS = ?, COMMISSIONING_DATE = ?, STATUS = ?, BUILDING_NAME = ?, " +
                        "FLOOR = ?, LOCATION = ? " +
                        "WHERE ID_GATEWAY = ?",
                gateway.getIpAddress(), gateway.getCommissioningDate(), gateway.getStatus(), gateway.getBuildingName(),
                gateway.getFloor(), gateway.getLocation(), gateway.getIdGateway()
        );
    }

}
