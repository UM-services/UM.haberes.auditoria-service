# Um.haberes.auditoria-service

[![UM.haberes.aud.rest CI](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-6DB33F?logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.4-6DB33F?logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white)
![MySQL Connector/J](https://img.shields.io/badge/MySQL%20Connector%2FJ-26.10.0-4479A1?logo=mysql&logoColor=white)
![SpringDoc OpenAPI](https://img.shields.io/badge/SpringDoc%20OpenAPI-3.1.1-blue)
![Log4j2](https://img.shields.io/badge/Log4j2-red)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

## Descripción
Servicio de auditoría para el sistema de haberes de la Universidad de Mendoza. Este microservicio se encarga de registrar y gestionar las operaciones de auditoría relacionadas con las transacciones y modificaciones en el sistema de haberes.

Versión actual: **1.0.1** (ver [CHANGELOG.md](CHANGELOG.md)).

## Tecnologías
- Java 25
- Spring Boot 4.1.2
- Spring Cloud 2025.1.4 (Consul discovery)
- Spring Boot Actuator
- Maven 3.8.8
- MySQL Connector/J 26.10.0
- SpringDoc OpenAPI 3.1.1
- Spring Security
- Lombok
- Log4j2
- GitHub Actions (CI/CD con SonarCloud)
- JaCoCo (cobertura de código)

## Requisitos previos
- Java JDK 25 o superior
- Maven 3.8.8 o superior
- MySQL 8.0 o superior
- Docker (opcional)

## Configuración
La configuración vive en `src/main/resources/bootstrap.yml` (mismo esquema que el resto de
los servicios UM). Todo se puede inyectar por variables de entorno:

| Variable | Default | Descripción |
|---|---|---|
| `APP_PORT` | `8210` | Puerto HTTP |
| `APP_SERVER` | `192.168.201.132:3306` | Host MySQL |
| `APP_DATABASE` | `haberes_aud` | Base de datos |
| `APP_USER` / `APP_PASSWORD` | `root` / `root` | Credenciales MySQL |
| `APP_API_KEY` | *(vacío)* | Clave para la ingesta (`X-API-Key`). Sin ella, la escritura responde 503 |
| `APP_SWAGGER_USER` / `APP_SWAGGER_PASSWORD` | `admin` / `password` | Basic auth de Swagger |
| `APP_CONSUL_HOST` / `APP_CONSUL_PORT` | `consul-service` / `8500` | Consul |
| `APP_LOGGING` | `info` | Nivel de log |

```yaml
app:
  port: ${APP_PORT:8210}
  server: ${APP_SERVER:192.168.201.132:3306}
  database: ${APP_DATABASE:haberes_aud}
  user: ${APP_USER:root}
  password: ${APP_PASSWORD:root}
  api_key: ${APP_API_KEY:}
  swagger_user: ${APP_SWAGGER_USER:admin}
  swagger_password: ${APP_SWAGGER_PASSWORD:password}
  consul:
    host: ${APP_CONSUL_HOST:consul-service}
    port: ${APP_CONSUL_PORT:8500}

spring:
  application:
    name: haberes-auditoria-service
  cloud:
    consul:
      discovery:
        health-check-path: /actuator/health/readiness
```

### Base de datos

El servicio **no crea ni migra el esquema** (`spring.jpa.hibernate.ddl-auto: none`). El DDL vive
versionado en `sql/` y lo aplica quien despliega:

```bash
mysql -h 192.168.201.132 -u root -p haberes_aud < sql/001_audit_event.sql
```

La base se llama `haberes_aud` (renombrada el 2026-10-26; el nombre viejo `haberes.aud` quedó
vacío y se puede dropear).

## Instalación y configuración

1. Clonar el repositorio:
```bash
git clone https://github.com/UM-services/UM.haberes.auditoria-service.git
```

2. Navegar al directorio del proyecto:
```bash
cd UM.haberes.auditoria-service
```

3. Compilar el proyecto:
```bash
mvn clean install
```

## Ejecución

### Local
```bash
mvn spring-boot:run
```

### Docker
```bash
docker build -t um-haberes-auditoria .
docker run -p 8210:8210 -e APP_SERVER=192.168.201.132:3306 um-haberes-auditoria
```

## Uso
El servicio expone endpoints REST para la gestión de auditorías. La documentación detallada de la API está disponible (basic auth con `APP_SWAGGER_USER` / `APP_SWAGGER_PASSWORD`) en:

```
http://localhost:8210/swagger-ui.html
```

### Endpoints disponibles

Ingesta (requiere `X-API-Key`):

| Método | Path | Descripción |
|---|---|---|
| `POST` | `/api/haberes/auditoria/evento/lote` | Lote de hasta 500 eventos; idempotente por `eventId` |
| `POST` | `/api/haberes/auditoria/evento/` | Un evento |

Consulta (red interna; JWT pendiente):

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/haberes/auditoria/evento/{eventId}` | Detalle |
| `GET` | `/api/haberes/auditoria/evento/correlacion/{correlationId}` | Todos los cambios de una operación |
| `GET` | `/api/haberes/auditoria/evento/entidad/{entity}/{entityId}` | Últimos 100 cambios de una entidad |
| `GET` | `/api/haberes/auditoria/evento/actor/{actorId}` | Últimos 100 eventos de un usuario |
| `GET` | `/api/haberes/auditoria/evento/periodo/{anho}/{mes}` | Eventos del período (paginado, 200 por página) |
| `POST` | `/api/haberes/auditoria/evento/search` | Búsqueda compuesta por body |

> El endpoint legacy `POST /log/` (SQL crudo) se retiró: las escrituras de Haberes pasan por
> `haberes-core` y se auditan como eventos estructurados. Los diagramas de arquitectura, flujo y
> modelo de datos se mantienen en `docs/diagrams/` y se publican en la documentación generada.

### Niveles y operaciones

| `level` | Cuándo | Contenido |
|---|---|---|
| `REQUEST` | Una fila por operación HTTP, incluso si falla | actor, IP, endpoint, `outcome`, `affectedRows` |
| `CHANGE` | Un evento por cambio de entidad | `entity`, `entityId`, `changedFields`, `beforeData`/`afterData` enmascarados |
| `SUMMARY` | Operación masiva resumida por el emisor | `entity`, `affectedRows` y `metadata.counts` (bajas/altas), período, filtro y `bulkId` |

`operation` admite `INSERT`, `UPDATE`, `DELETE`, `ACCESS` y `BULK`. Para `operation=BULK` el
servicio exige `affectedRows > 0`; para `CHANGE` y `SUMMARY`, `entity` no puede ser vacío. El
buscador compuesto permite filtrar por `level` y `operation`.

## Integración en el stack
- Se registra en Consul como `haberes-auditoria-service` (tags `haberes,auditoria`) y expone
  `GET /actuator/health/readiness` para el health check.
- El gateway de haberes publica la ruta `/api/haberes/auditoria/**` → `lb://haberes-auditoria-service`.
- En el compose local corre en el puerto `8210`.

## Estructura del proyecto
```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── um/haberes/aud/rest/
│   │   │       ├── configuration/
│   │   │       │   ├── AuditApiKeyFilter.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── banner.txt
│   │       └── bootstrap.yml
│   └── test/
├── docs/
│   └── diagrams/
├── sql/
│   └── 001_audit_event.sql
├── .github/
│   └── workflows/
│       ├── deploy-develop.yml
│       ├── deploy-staging.yml
│       ├── generate-docs.yml
│       └── maven.yml
├── .dockerignore
├── .gitignore
├── Dockerfile
├── pom.xml
├── README.md
└── CHANGELOG.md
```

## Logging
El servicio usa Log4j2 (`spring-boot-starter-log4j2`, con `spring-boot-starter-logging` excluido).
El repositorio no incluye un archivo de configuración de Log4j2 (`src/main/resources` sólo contiene
`banner.txt` y `bootstrap.yml`), por lo que la salida y el formato quedan a cargo de la configuración
por defecto de Spring Boot. El nivel se controla con la variable `APP_LOGGING` (mapeada a
`app.logging` en `bootstrap.yml`).

## Contribución
1. Fork el proyecto
2. Cree una rama para su feature (`git checkout -b feature/AmazingFeature`)
3. Commit sus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abra un Pull Request

## Contacto
Universidad de Mendoza - [https://um.edu.ar](https://um.edu.ar)

## Licencia
Este proyecto está bajo la Licencia MIT - vea el archivo [LICENSE.md](LICENSE.md) para más detalles.

## Autor
✍️ Desarrollado por Daniel Quinteros
