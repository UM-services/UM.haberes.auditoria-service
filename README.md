# Um.haberes.auditoria-service

[![UM.haberes.aud.rest CI](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/UM-services/UM.haberes.auditoria-service/actions/workflows/maven.yml)

## Descripción
Servicio de auditoría para el sistema de haberes de la Universidad de Mendoza. Este microservicio se encarga de registrar y gestionar las operaciones de auditoría relacionadas con las transacciones y modificaciones en el sistema de haberes.

## Tecnologías
- Java
- Spring Boot
- Maven
- GitHub Actions (CI/CD)

## Requisitos previos
- Java JDK 11 o superior
- Maven 3.6 o superior
- Docker (opcional)

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

## Estructura del proyecto
```
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── .github/
├── .gitignore
├── pom.xml
└── README.md
```

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
