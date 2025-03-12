package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GatewayDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Récupère la liste de toutes les passerelles enregistrées dans la base de données.
     *
     * @return une liste d'objets {@code Gateway} représentant toutes les passerelles.
     */
    public List<Gateway> findAllGateways() {
        return jdbcTemplate.query(
                "SELECT * FROM GATEWAYS;",
                new BeanPropertyRowMapper<Gateway>(Gateway.class));
    }

    /**
     * Récupère une passerelle spécifique en fonction de son identifiant.
     *
     * @param id l'identifiant de la passerelle à rechercher.
     * @return un objet {@code Gateway} correspondant à l'ID fourni.
     * @throws EmptyResultDataAccessException si aucune passerelle ne correspond à l'ID.
     */
    public Gateway findByIdOfGateway(String id) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM GATEWAYS WHERE ID_GATEWAY=?",
                new Object[]{id},
                new BeanPropertyRowMapper<Gateway>(Gateway.class));
    }

    /**
     * Supprime une passerelle de la base de données en fonction de son identifiant.
     *
     * @param id l'identifiant de la passerelle à supprimer
     * @return le nombre de lignes affectées par la suppression (généralement 0 ou 1)
     */
    public int deleteByIdOfGateway(String id) {
        return jdbcTemplate.update(
                "DELETE FROM GATEWAYS WHERE ID_GATEWAY=?",
                new Object[]{id});
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
                        "ID_GATEWAY, COMMISSIONING_DATE, STATUS, " +
                        "BATIMENT_NAME, ETAGE, EMPLACEMENT) " +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                new Object[]{ gateway.getIdGateway(), gateway.getCommissioningDate(),
                gateway.getStatus(), gateway.getBatimentName(), gateway.getEtage(), gateway.getEmplacement() }
        );
    }

    /**
     * Met à jour les informations d'une passerelle dans la base de données.
     *
     * @param gateway L'objet {@link Gateway} contenant les nouvelles valeurs à mettre à jour.
     * @return Le nombre de lignes affectées par la mise à jour (1 si l'ID existe, 0 sinon).
     */
    public int updateGateway(Gateway gateway) {
        return jdbcTemplate.update(
                "UPDATE GATEWAYS SET " +
                        "COMMISSIONING_DATE = ?, STATUS = ?, BATIMENT_NAME = ?, " +
                        "ETAGE = ?, EMPLACEMENT = ? " +
                        "WHERE ID_GATEWAY = ?",
                new Object[]{ gateway.getCommissioningDate(), gateway.getStatus(), gateway.getBatimentName(),
                gateway.getEtage(), gateway.getEmplacement(), gateway.getIdGateway() }
        );
    }

}
