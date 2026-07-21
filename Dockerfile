# Etapa de construcción: Entramos a webapp y compilamos con Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN cd webapp && mvn clean package -DskipTests

# Etapa de ejecución: Tomamos el archivo jar generado dentro de webapp
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/webapp/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
