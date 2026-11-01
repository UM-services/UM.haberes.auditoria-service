package um.haberes.aud.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.aud.rest.model.ActorTrust;
import um.haberes.aud.rest.model.ActorType;
import um.haberes.aud.rest.model.AuditLevel;
import um.haberes.aud.rest.model.AuditOperation;
import um.haberes.aud.rest.model.AuditOutcome;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Evento que envía un servicio emisor.
 *
 * <p>El emisor no manda {@code receivedAt} ni el hash: los calcula el servicio de
 * auditoría. Tampoco se confía en el reloj del cliente para ordenar: {@code occurredAt}
 * se registra tal cual (con control de desvío) y {@code receivedAt} lo pone el servidor.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventRequest {

    @NotBlank
    @Size(max = 36)
    private String eventId;

    @NotNull
    private OffsetDateTime occurredAt;

    @Size(max = 64)
    private String correlationId;

    @NotBlank
    @Size(max = 48)
    private String service;

    @NotNull
    private AuditLevel level;

    @NotNull
    private AuditOperation operation;

    @Size(max = 64)
    private String entity;

    @Size(max = 64)
    private String entityId;

    @NotNull
    private ActorType actorType;

    private Long actorId;

    @Size(max = 64)
    private String actorLogin;

    @NotNull
    private ActorTrust actorTrust;

    @Size(max = 45)
    private String clientIp;

    @Size(max = 8)
    private String httpMethod;

    @Size(max = 255)
    private String endpoint;

    @NotNull
    private AuditOutcome outcome;

    private Integer httpStatus;

    private Integer affectedRows;

    private List<String> changedFields;

    private Map<String, Object> beforeData;

    private Map<String, Object> afterData;

    private Map<String, Object> metadata;
}
