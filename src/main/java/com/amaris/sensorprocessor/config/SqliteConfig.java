//package com.amaris.sensorprocessor.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@ConfigurationProperties
//public class SqliteConfig {
//
//    private final Environment env;
//
//    @Autowired
//    public SqliteConfig(Environment env) {
//        this.env = env;
//    }
//
//    /**
//     * Crée et retourne une instance de {@link DataSource} configurée à partir des propriétés de l'environnement.
//     * Cette méthode utilise un {@link DriverManagerDataSource} pour se connecter à la base de données
//     * en utilisant les informations (URL, utilisateur, mot de passe) extraites des propriétés de l'environnement,
//     * issues du fichier application.properties
//     *
//     * @return Une instance de {@link DataSource} configurée avec les informations de connexion.
//     */
//    @Bean
//    public DataSource dataSource() {
//        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(env.getProperty("driverClassName"));
//        dataSource.setUrl(env.getProperty("url"));
//        dataSource.setUsername(env.getProperty(""));
//        dataSource.setPassword(env.getProperty(""));
//        return dataSource;
//    }
//
//}
