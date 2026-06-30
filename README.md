# EstudiaYa

Proyecto con backend en Spring Boot y frontend en Angular.

## Puertos

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`
- Login Angular: `http://localhost:4200/login`

## Antes de correr

Necesitas tener instalado:

- Java 17 o superior
- Node.js y npm
- MySQL corriendo en `localhost:3306`

La base de datos configurada es `estudia_ya`.

Si tu usuario o contrasena de MySQL son diferentes, cambia estos datos en:

`webapp/src/main/resources/application.properties`

Datos actuales configurados:

```properties
spring.datasource.username=root
spring.datasource.password=
```

Si tu MySQL usa otra contrasena, escribe el valor despues del signo `=`.

## Correr el backend

Abre una terminal en la carpeta del proyecto y ejecuta:

```powershell
cd webapp
.\mvnw.cmd spring-boot:run
```

El backend debe quedar corriendo en:

`http://localhost:8080`

## Correr el frontend

Abre otra terminal en la carpeta del proyecto y ejecuta:

```powershell
cd frontend
npm install
npm start
```

`npm install` solo es necesario la primera vez o cuando cambien las dependencias.

El frontend debe quedar corriendo en:

`http://localhost:4200`

## Probar el login

Entra a:

`http://localhost:4200/login`

Usuario demo:

- Email: `carlos@estudiaya.pe`
- Contrasena: `123456`

El login de Angular llama al backend en:

`http://localhost:8080/api/auth/login`

Si los datos son correctos, Angular guarda el token y redirige al dashboard:

`http://localhost:8080/`
