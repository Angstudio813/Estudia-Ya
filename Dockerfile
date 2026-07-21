# Etapa de construcción (Build)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN cd webapp && mvn clean package -DskipTests

# Etapa de ejecución (Run) corregida con la etiqueta oficial existente
FROM eclipse-temurin:17-jre-slim
WORKDIR /app
COPY --from=build /app/webapp/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
