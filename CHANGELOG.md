# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-10-26

### Agregado
- feat: modelo de auditoría estructurada `audit_event` (append-only). DDL versionado en `sql/001_audit_event.sql` (sin Flyway).
- feat: ingesta por lote (`POST /api/haberes/auditoria/evento/lote`, hasta 500 eventos) idempotente por `eventId`.
- feat: ingesta unitaria (`POST /api/haberes/auditoria/evento/`) con respuesta `202 Accepted`.
- feat: API de consulta por evento, correlación, entidad, actor, período y búsqueda compuesta (`POST /api/haberes/auditoria/evento/search`).
- feat: nuevo nivel `SUMMARY` para operaciones masivas y validación de `operation=BULK` (`affectedRows > 0` obligatorio, `entity` obligatorio para `CHANGE` y `SUMMARY`); el buscador compuesto permite filtrar por `level`/`operation`. Cambio aditivo, sin migración: `level` es `VARCHAR(8)` y `BULK` ya existía en el enum de operaciones (basado en `model/AuditLevel.java`, `service/AuditEventService.java`).
- feat: autenticación de la ingesta con `X-API-Key` (`APP_API_KEY`); la escritura responde `503` si la clave no está configurada y `401` si falta o no coincide.
- feat: validación de payloads, hashing canónico SHA-256 (`payload_hash`) y manejo de errores con `ProblemDetail`.
- feat: integración con Spring Cloud Consul (registro como `haberes-auditoria-service`, readiness en `/actuator/health/readiness`).
- feat: Spring Boot Actuator para health checks.
- feat: ruta pública `/api/haberes/auditoria/**` publicada por el gateway de haberes.
- feat: workflows de despliegue para `develop` y `staging` con verificación Maven, build/push de imagen y deploy en runners self-hosted.
- build: `bootstrap.yml` en reemplazo de `application.yml`, con host/base/credenciales inyectables por variables `APP_*`.
- build: base `haberes_aud` (antes `haberes.aud`).
- build: puerto por defecto 8210.
- build: `.dockerignore` para excluir `target/`, `.git/` y archivos de IDE del contexto Docker.
- test: datasource H2, Consul deshabilitado y `spring-boot-starter-webmvc-test` en el contexto de test; tests de ingesta idempotente y consultas.
- test: ingesta de `SUMMARY/BULK` con búsqueda por nivel y operación, y rechazo de `BULK` sin `affectedRows` (basado en `src/test/java/um/haberes/aud/rest/AuditEventControllerTest.java`).

### Cambiado
- build: Spring Boot de 4.0.5 a 4.1.2.
- build: Spring Cloud 2025.1.4 (`spring-cloud-starter-consul-discovery` y `spring-cloud-starter-bootstrap`).
- build: MySQL Connector/J de 9.6.0 a 26.10.0.
- build: SpringDoc OpenAPI de 3.0.2 a 3.1.1.
- build: Apache Commons Lang3 de 3.20.0 a 3.21.0.
- build: JaCoCo de 0.8.13 a 0.8.15.
- refactor: `SecurityConfig` deja la lectura interna abierta, publica los health checks y protege Swagger con basic auth; la escritura queda a cargo de `AuditApiKeyFilter`.

### Eliminado
- BREAKING: endpoint legacy `POST /log/` y modelo `Log`/`Auditable` (SQL crudo). La auditoría de
  Haberes se registra como eventos estructurados desde `haberes-core`.

## [0.1.2] - 2026-04-04

### Cambiado
- build: Actualizado Spring Boot de 4.0.2 a 4.0.5
- build: Actualizado SpringDoc OpenAPI de 3.0.1 a 3.0.2
- ci: Actualizado actions/checkout de v4 a v6
- ci: Actualizado actions/setup-java de v4 a v5
- ci: Actualizado actions/cache de v4 a v5
- ci: Actualizado actions/upload-pages-artifact de v3 a v4
- ci: Actualizado actions/deploy-pages de v4 a v5
- ci: Actualizado docker/login-action de v3 a v4
- ci: Actualizado docker/metadata-action de v5 a v6
- ci: Actualizado docker/setup-buildx-action de v3 a v4
- ci: Actualizado docker/build-push-action de v6 a v7
- ci: Eliminados comentarios de documentación en maven.yml

## [0.1.1] - 2026-02-02

### Cambiado
- build: Actualizado Spring Boot de 4.0.0-M3 a 4.0.2
- build: Actualizado MySQL Connector/J de 9.4.0 a 9.6.0
- build: Actualizado SpringDoc OpenAPI de 3.0.0-M1 a 3.0.1
- build: Actualizado Apache Commons Lang3 de 3.19.0 a 3.20.0
- build: Eliminada configuración executable del Spring Boot Maven Plugin

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