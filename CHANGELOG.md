# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-10-04

### Agregado
- feat: Configuración de seguridad con Spring Security (autenticación básica para Swagger UI)
- feat: Endpoints de consulta para logs de auditoría (GET /log/, GET /log/{id})
- feat: Workflow de generación de documentación automática con Mermaid
- feat: Banner personalizado en aplicación
- feat: Integración con SonarCloud para análisis de código
- feat: Cobertura de código con JaCoCo

### Cambiado
- build: Actualizado Spring Boot de 3.4.4 a 4.0.0-M3
- build: Actualizado Java de 21 a 25
- build: Actualizado MySQL Connector/J de 9.2.0 a 9.4.0
- build: Actualizado SpringDoc OpenAPI de 2.8.6 a 3.0.0-M1
- build: Mejorado Dockerfile con multi-stage build y usuario no privilegiado
- refactor: Cambiado sistema de logging de Logback a Log4j2 (eliminado logback-spring.xml)
- refactor: Actualizado CI/CD con caching y análisis SonarCloud

### Eliminado
- fix: Removido spring-boot-starter-hateoas (dependencia no utilizada)

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