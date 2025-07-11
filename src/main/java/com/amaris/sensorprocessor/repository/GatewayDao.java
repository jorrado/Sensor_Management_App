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
     * Récupère la liste complète des gateways triée par ID (insensible à la casse).
     * Utilise BeanPropertyRowMapper pour convertir chaque ligne SQL en objet Gateway.
     *
     * @return Liste de toutes les gateways, vide si aucune trouvée
     */
    public List<Gateway> findAllGateways() {
        return jdbcTemplate.query(
                "SELECT * FROM GATEWAYS ORDER BY LOWER(ID_GATEWAY) ASC;",
                new BeanPropertyRowMapper<Gateway>(Gateway.class));
    }

    /**
     * Recherche une gateway par son ID.
     * Exécute une requête SQL et mappe le résultat en Gateway.
     * Renvoie un Optional vide si aucune gateway trouvée.
     *
     * @param idGateway Identifiant de la gateway recherchée
     * @return Optional contenant la gateway si trouvée, sinon vide
     */
    public Optional<Gateway> findByIdOfGateway(String idGateway) {
        List<Gateway> gateways = jdbcTemplate.query(
                "SELECT * FROM GATEWAYS WHERE ID_GATEWAY=?",
                new BeanPropertyRowMapper<>(Gateway.class),
                idGateway);

        return gateways.isEmpty() ? Optional.empty() : Optional.of(gateways.get(0));
    }

    /**
     * Supprime une gateway en base via son ID.
     * Exécute une requête DELETE SQL.
     *
     * @param idGateway Identifiant de la gateway à supprimer
     * @return Nombre de lignes affectées (0 si aucune suppression)
     */
    public int deleteByIdOfGateway(String idGateway) {
        return jdbcTemplate.update(
                "DELETE FROM GATEWAYS WHERE ID_GATEWAY=?",
                idGateway);
    }

    /**
     * Insère une nouvelle gateway en base de données.
     * Utilise jdbcTemplate.update avec les champs de l'objet Gateway.
     *
     * @param gateway Objet Gateway à insérer
     * @return Nombre de lignes insérées (1 si succès)
     */
    public int insertGateway(Gateway gateway) {
        return jdbcTemplate.update(
                "INSERT INTO GATEWAYS (" +
                        "ID_GATEWAY, IP_ADDRESS, COMMISSIONING_DATE, " +
                        "BUILDING_NAME, FLOOR, LOCATION) " +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                gateway.getIdGateway(), gateway.getIpAddress(), gateway.getCommissioningDate(),
                gateway.getBuildingName(), gateway.getFloor(), gateway.getLocation()
        );
    }

    /**
     * Met à jour les informations d'une gateway existante en base.
     * Modifie les champs sauf l'ID qui sert de critère.
     *
     * @param gateway Objet Gateway avec les nouvelles données
     * @return Nombre de lignes modifiées (0 si aucune correspondance)
     */
    public int updateGateway(Gateway gateway) {
        return jdbcTemplate.update(
                "UPDATE GATEWAYS SET " +
                        "IP_ADDRESS = ?, COMMISSIONING_DATE = ?, BUILDING_NAME = ?, " +
                        "FLOOR = ?, LOCATION = ? " +
                        "WHERE ID_GATEWAY = ?",
                gateway.getIpAddress(), gateway.getCommissioningDate(),
                gateway.getBuildingName(), gateway.getFloor(), gateway.getLocation(), gateway.getIdGateway()
        );
    }

}
