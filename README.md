# Um.haberes.auditoria-service

[![UM.haberes.aud.rest CI](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml)

## Descripción
Servicio de auditoría para el sistema de haberes de la Universidad de Mendoza. Este microservicio se encarga de registrar y gestionar las operaciones de auditoría relacionadas con las transacciones y modificaciones en el sistema de haberes.

## Tecnologías
- Java 21
- Spring Boot 3.4.4
- Maven 3.8.8
- MySQL 9.2.0
- SpringDoc OpenAPI 2.8.6
- Lombok
- Log4j2
- GitHub Actions (CI/CD)

## Requisitos previos
- Java JDK 21 o superior
- Maven 3.8.8 o superior
- MySQL 8.0 o superior
- Docker (opcional)

## Configuración
El servicio requiere las siguientes variables de entorno o configuración en `application.yml`:
```yaml
app:
  port: 8080
  logging: debug
  server: localhost:3306
  database: haberes.aud
  user: root
  password: root
```

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
docker run -p 8080:8080 um-haberes-auditoria
```

## Uso
El servicio expone endpoints REST para la gestión de auditorías. La documentación detallada de la API está disponible en:

```
http://localhost:8080/swagger-ui.html
```

### Endpoints disponibles
- POST `/log/` - Registrar un nuevo log de auditoría

## Estructura del proyecto
```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── um/haberes/aud/rest/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── configuration/
│   │   └── resources/
│   └── test/
├── .github/
├── .gitignore
├── pom.xml
└── README.md
```

## Logging
El servicio utiliza Log4j2 para el manejo de logs con las siguientes características:
- Logs diarios con rotación por tamaño (10MB)
- Formato personalizado con timestamp, nivel, thread y mensaje
- Almacenamiento en directorio `./logs`

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
