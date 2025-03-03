# # Utiliser une image OpenJDK 17 basée sur Alpine Linux
# FROM openjdk:17-jdk-alpine

# # Définir le répertoire de travail
# WORKDIR /app

# # Copier le fichier JAR généré dans l'image Docker
# COPY target/Foyer-0.0.1-SNAPSHOT.jar app.jar

# # Exposer le port sur lequel l'application écoute
# EXPOSE 8081

# # Définir la commande pour exécuter l'application
# ENTRYPOINT ["java", "-jar", "app.jar"]

# Utiliser une image OpenJDK 17 basée sur Alpine Linux
FROM openjdk:17-jdk-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré dans l'image Docker
COPY target/Foyer-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application écoute
EXPOSE 8081

# Définir la commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
