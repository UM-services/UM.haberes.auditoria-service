# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.1-SNAPSHOT] - 2024-03-21

### Agregado
- Configuración inicial del proyecto Spring Boot
- Implementación del modelo de auditoría con JPA
- Configuración de logging con Log4j2
- Integración con MySQL
- Documentación de API con SpringDoc OpenAPI
- Configuración de CI/CD con GitHub Actions
- Dockerfile para containerización
- Endpoint básico para registro de logs

### Características
- Registro automático de fechas de creación y modificación
- Rotación de logs por tamaño y fecha
- Documentación automática de API
- Integración con el sistema de haberes de la UM

### Dependencias
- Spring Boot 3.4.4
- Java 21
- MySQL 9.2.0
- SpringDoc OpenAPI 2.8.6
- Lombok
- Log4j2

### Configuración
- Puerto por defecto: 8080
- Base de datos: MySQL
- Logging: Log4j2 con rotación
- Documentación: Swagger UI 