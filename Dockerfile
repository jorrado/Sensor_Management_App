# Utiliser une image de base avec OpenJDK
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /opt/app

# Copier le fichier JAR dans le conteneur
COPY target/sensorprocessor-0.0.1-SNAPSHOT.jar /opt/app/sensorprocessor.jar

# Exposer le port de l'application (si nécessaire)
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "sensorprocessor.jar"]
