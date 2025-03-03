                                          Dockerfile                                                     # Utiliser une image Java
FROM openjdk:17-jdk-slim

# Copier le fichier JAR généré par Maven ou Gradle
COPY target/Foyer-0.0.1-SNAPSHOT.jar Foyer.jar

# Exposer le port utilisé par l'application
EXPOSE 8081

# Exécuter l'application
ENTRYPOINT ["java", "-jar", "Foyer.jar"]
