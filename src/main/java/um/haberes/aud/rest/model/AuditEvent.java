package um.haberes.aud.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Evento de auditoría append-only.
 *
 * <p>Se guarda en UTC como {@code DATETIME(6)}. Los campos JSON se persisten como
 * texto: el motor (MySQL) los castea a {@code JSON} y el esquema real lo administra
 * el DDL de {@code sql/}, no Hibernate.</p>
 */
@Entity
@Table(name = "audit_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    @Id
    @Column(name = "event_id", length = 36, nullable = false)
    private String eventId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "service", length = 48, nullable = false)
    private String service;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 8, nullable = false)
    private AuditLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", length = 16, nullable = false)
    private AuditOperation operation;

    @Column(name = "entity", length = 64)
    private String entity;

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", length = 12, nullable = false)
    private ActorType actorType;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_login", length = 64)
    private String actorLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_trust", length = 12, nullable = false)
    private ActorTrust actorTrust;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "http_method", length = 8)
    private String httpMethod;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 12, nullable = false)
    private AuditOutcome outcome;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "affected_rows")
    private Integer affectedRows;

    @Column(name = "changed_fields", length = 65535)
    private String changedFields;

    @Column(name = "before_data", length = 65535)
    private String beforeData;

    @Column(name = "after_data", length = 65535)
    private String afterData;

    @Column(name = "payload_hash", length = 64)
    private String payloadHash;

    @Column(name = "metadata", length = 65535)
    private String metadata;
}
