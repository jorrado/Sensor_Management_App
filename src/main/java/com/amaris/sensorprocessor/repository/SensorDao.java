package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SensorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SensorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
     */
    public Optional<Sensor> findByIdOfSensor(String id) {
        List<Sensor> sensors = jdbcTemplate.query(
                "SELECT * FROM SENSORS WHERE ID_SENSOR=?",
                new BeanPropertyRowMapper<Sensor>(Sensor.class),
                id);

        return sensors.isEmpty() ? Optional.empty() : Optional.of(sensors.get(0));
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
                id);
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
                sensor.getIdSensor(), sensor.getDeviceType(), sensor.getCommissioningDate(),
                sensor.getStatus(), sensor.getBatimentName(), sensor.getEtage(), sensor.getEmplacement(), sensor.getIdGateway()
        );
    }

    /**
     * Met à jour les informations d'un capteur dans la base de données.
     *
     * @param sensor l'objet {@link Sensor} contenant les nouvelles valeurs à mettre à jour.
     * @return le nombre de lignes affectées par la mise à jour (1 si l'id existe, 0 sinon).
     */
    public int updateSensor(Sensor sensor) {
        return jdbcTemplate.update(
                "UPDATE SENSORS SET " +
                        "COMMISSIONING_DATE = ?, STATUS = ?, BATIMENT_NAME = ?, " +
                        "ETAGE = ?, EMPLACEMENT = ?, ID_GATEWAY = ? " +
                        "WHERE ID_SENSOR = ?",
                sensor.getCommissioningDate(), sensor.getStatus(), sensor.getBatimentName(),
                        sensor.getEtage(), sensor.getEmplacement(), sensor.getIdGateway(),
                        sensor.getIdSensor()
        );
    }

}
