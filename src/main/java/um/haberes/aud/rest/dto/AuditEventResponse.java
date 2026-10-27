package um.haberes.aud.rest.dto;

import tools.jackson.databind.JsonNode;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventResponse {

    private String eventId;
    private OffsetDateTime occurredAt;
    private OffsetDateTime receivedAt;
    private String correlationId;
    private String service;
    private AuditLevel level;
    private AuditOperation operation;
    private String entity;
    private String entityId;
    private ActorType actorType;
    private Long actorId;
    private String actorLogin;
    private ActorTrust actorTrust;
    private String clientIp;
    private String httpMethod;
    private String endpoint;
    private AuditOutcome outcome;
    private Integer httpStatus;
    private Integer affectedRows;
    private List<String> changedFields;
    private JsonNode beforeData;
    private JsonNode afterData;
    private String payloadHash;
    private JsonNode metadata;
}
