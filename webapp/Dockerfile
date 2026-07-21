# --- ETAPA DE CONSTRUCCIÓN (BUILD) ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven y dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y empaquetar la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA DE EJECUCIÓN (RUN) ---
FROM openjdk:17-slim
WORKDIR /app

# Copiar el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que corre la aplicación (ajusta si usas otro, por ejemplo 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
