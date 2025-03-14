package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * A utiliser avec DataSourceUtils.getConnection(dataSource)
     */
//    @Autowired
//    DataSource dataSource;

    /**
     * Récupère la liste de tous les utilisateurs enregistrés dans la base de données.
     *
     * @return une liste d'objets {@code User} représentant tous les utilisateurs.
     */
    public List<User> findAllUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM USERS;",
                new BeanPropertyRowMapper<User>(User.class));
    }

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur.
     * @return L'objet `User` correspondant à l'utilisateur trouvé.
     * @throws EmptyResultDataAccessException si résultat vide.
     * @throws IncorrectResultSizeDataAccessException si plusieurs résultat.
     */
    public User findByUsername(String username)
            throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException { // RuntimeException
        return jdbcTemplate.queryForObject(
                "SELECT * FROM USERS WHERE USERNAME=?",
                new Object[]{username},
                new BeanPropertyRowMapper<User>(User.class));
    }

    /**
     * Supprime un utilisateur de la base de données en fonction de son identifiant.
     *
     * @param username l'identifiant de l'utilisateur à supprimer
     * @return le nombre de lignes affectées par la suppression (généralement 0 ou 1)
     */
    public int deleteByIdOfUser(String username) {
        return jdbcTemplate.update(
                "DELETE FROM USERS WHERE USERNAME=?",
                new Object[]{username});
    }

    /**
     * Insère un nouvel utilisateur dans la base de données.
     *
     * @param user l'objet {@link User} contenant les informations de l'utilisateur à insérer.
     * @return le nombre de lignes affectées dans la base de données (1 si l'insertion a réussi, 0 sinon).
     */
    public int insertUser(User user) {
        return jdbcTemplate.update(
                "INSERT INTO USERS (" +
                        "ID_USER, USERNAME, FIRSTNAME, " +
                        "LASTNAME, PASSWORD, ROLE, EMAIL) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)",
                new Object[]{ user.getIdUser(), user.getUsername(), user.getFirstname(),
                user.getLastname(), user.getPassword(), user.getRole(), user.getEmail() }
        );
    }

    /**
     * Met à jour les informations d'un utilisateur dans la base de données.
     *
     * @param user L'objet {@link User} contenant les nouvelles valeurs à mettre à jour.
     * @return Le nombre de lignes affectées par la mise à jour (1 si l'ID existe, 0 sinon).
     */
    public int updateUser(User user) {
        return jdbcTemplate.update(
                "UPDATE USERS SET " +
                        "USERNAME = ?, PASSWORD = ?, ROLE = ?, " +
                        "EMAIL = ? " +
                        "WHERE ID_USER = ?",
                new Object[]{ user.getUsername(), user.getPassword(), user.getRole(),
                user.getEmail(), user.getIdUser() }
        );
    }

}
