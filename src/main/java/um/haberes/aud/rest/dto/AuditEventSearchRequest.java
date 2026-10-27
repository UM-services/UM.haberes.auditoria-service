package um.haberes.aud.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.aud.rest.model.AuditLevel;
import um.haberes.aud.rest.model.AuditOperation;
import um.haberes.aud.rest.model.AuditOutcome;

import java.time.OffsetDateTime;

/**
 * Búsqueda compuesta por body (nunca query params).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventSearchRequest {

    private OffsetDateTime from;

    private OffsetDateTime to;

    @Size(max = 48)
    private String service;

    private AuditLevel level;

    private AuditOperation operation;

    private AuditOutcome outcome;

    private Long actorId;

    @Size(max = 64)
    private String entity;

    @Size(max = 64)
    private String entityId;

    @Size(max = 64)
    private String correlationId;

    @Min(0)
    private Integer page;

    @Min(1)
    @Max(200)
    private Integer size;
}
