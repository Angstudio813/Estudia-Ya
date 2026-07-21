# --- CONSTRUCCIÓN Y EJECUCIÓN CON MAVEN ---
FROM maven:3.8.5-openjdk-17
WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Exponer el puerto de la aplicación (ajusta si usas 8080 u otro)
EXPOSE 8080

# Comando para ejecutar el jar generado automáticamente
CMD ["java", "-jar", "target/Estudia-Ya-0.0.1-SNAPSHOT.jar"]
