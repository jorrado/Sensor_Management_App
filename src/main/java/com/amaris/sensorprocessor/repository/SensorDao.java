package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SensorDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Récupère la liste de tous les capteurs enregistrés dans la base de données.
     *
     * @return une liste d'objets {@code Sensor} représentant tous les capteurs.
     */
    public List<Sensor> findAllSensors() {
        return jdbcTemplate.query(
                "SELECT * FROM SENSORS;",
                new BeanPropertyRowMapper<Sensor>(Sensor.class));
    }

    /**
     * Récupère un capteur spécifique en fonction de son identifiant.
     *
     * @param id l'identifiant du capteur à rechercher.
     * @return un objet {@code Sensor} correspondant à l'ID fourni.
     * @throws EmptyResultDataAccessException si aucun capteur ne correspond à l'ID.
     */
    public Sensor findByIdOfSensor(String id) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM SENSORS WHERE ID_SENSOR=?",
                new Object[]{id},
                new BeanPropertyRowMapper<Sensor>(Sensor.class));
    }

    /**
     * Supprime un capteur de la base de données en fonction de son identifiant.
     *
     * @param id l'identifiant du capteur à supprimer
     * @return le nombre de lignes affectées par la suppression (généralement 0 ou 1)
     */
    public int deleteByIdOfSensor(String id) {
        return jdbcTemplate.update(
                "DELETE FROM SENSORS WHERE ID_SENSOR=?",
                new Object[]{id});
    }

    /**
     * Insère un nouveau capteur dans la base de données.
     *
     * @param sensor l'objet {@link Sensor} contenant les informations du capteur à insérer.
     * @return le nombre de lignes affectées dans la base de données (1 si l'insertion a réussi, 0 sinon).
     */
    public int insertSensor(Sensor sensor) {
        return jdbcTemplate.update(
                "INSERT INTO SENSORS (" +
                        "ID_SENSOR, DEVICE_TYPE, COMMISSIONING_DATE, " +
                        "STATUS, BATIMENT_NAME, ETAGE, EMPLACEMENT, ID_GATEWAY) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{ sensor.getIdSensor(), sensor.getDeviceType(), sensor.getCommissioningDate(),
                sensor.getStatus(), sensor.getBatimentName(), sensor.getEtage(), sensor.getEmplacement(), sensor.getIdGateway() }
        );
    }

    /**
     * Met à jour les informations d'un capteur dans la base de données.
     *
     * @param sensor L'objet {@link Sensor} contenant les nouvelles valeurs à mettre à jour.
     * @return Le nombre de lignes affectées par la mise à jour (1 si l'ID existe, 0 sinon).
     */
    public int updateSensor(Sensor sensor) {
        return jdbcTemplate.update(
                "UPDATE SENSORS SET " +
                        "COMMISSIONING_DATE = ?, STATUS = ?, BATIMENT_NAME = ?, " +
                        "ETAGE = ?, EMPLACEMENT = ?, ID_GATEWAY = ? " +
                        "WHERE ID_SENSOR = ?",
                new Object[]{ sensor.getCommissioningDate(), sensor.getStatus(), sensor.getBatimentName(),
                        sensor.getEtage(), sensor.getEmplacement(), sensor.getIdGateway(),
                        sensor.getIdSensor() }
        );
    }

}
