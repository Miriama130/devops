# Utiliser une image officielle de Java comme base
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré par Maven
COPY target/Foyer-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application s'exécute
EXPOSE 8081

# Commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]
