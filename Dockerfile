# Utiliser une image Java basée sur OpenJDK 17
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré par Maven dans l'image Docker
COPY target/Foyer-0.0.1-SNAPSHOT.jar /app/foyer-app.jar

# Exposer le port utilisé par l'application (ici 8081)
EXPOSE 8081

# Exécuter l'application Java avec le fichier JAR
ENTRYPOINT ["java", "-jar", "foyer-app.jar"]