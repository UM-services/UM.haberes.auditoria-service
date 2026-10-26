package um.haberes.aud.rest.service;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.aud.rest.dto.AuditEventRequest;
import um.haberes.aud.rest.dto.AuditEventResponse;
import um.haberes.aud.rest.model.AuditEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditEventMapper {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final AuditPayloadHasher hasher;

    public AuditEvent toEntity(AuditEventRequest request, LocalDateTime receivedAt) {
        LocalDateTime occurredAt = request.getOccurredAt()
                .withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        String changedFields = write(request.getChangedFields());
        String beforeData = write(request.getBeforeData());
        String afterData = write(request.getAfterData());
        String metadata = write(request.getMetadata());

        return AuditEvent.builder()
                .eventId(request.getEventId())
                .occurredAt(occurredAt)
                .receivedAt(receivedAt)
                .correlationId(request.getCorrelationId())
                .service(request.getService())
                .level(request.getLevel())
                .operation(request.getOperation())
                .entity(request.getEntity())
                .entityId(request.getEntityId())
                .actorType(request.getActorType())
                .actorId(request.getActorId())
                .actorLogin(request.getActorLogin())
                .actorTrust(request.getActorTrust())
                .clientIp(request.getClientIp())
                .httpMethod(request.getHttpMethod())
                .endpoint(request.getEndpoint())
                .outcome(request.getOutcome())
                .httpStatus(request.getHttpStatus())
                .affectedRows(request.getAffectedRows())
                .changedFields(changedFields)
                .beforeData(beforeData)
                .afterData(afterData)
                .metadata(metadata)
                .payloadHash(hasher.hash(
                        request.getEventId(),
                        occurredAt,
                        request.getCorrelationId(),
                        request.getService(),
                        name(request.getLevel()),
                        name(request.getOperation()),
                        request.getEntity(),
                        request.getEntityId(),
                        name(request.getActorType()),
                        request.getActorId(),
                        request.getActorLogin(),
                        name(request.getActorTrust()),
                        request.getClientIp(),
                        request.getHttpMethod(),
                        request.getEndpoint(),
                        name(request.getOutcome()),
                        request.getHttpStatus(),
                        request.getAffectedRows(),
                        changedFields,
                        beforeData,
                        afterData))
                .build();
    }

    public AuditEventResponse toResponse(AuditEvent event) {
        return AuditEventResponse.builder()
                .eventId(event.getEventId())
                .occurredAt(event.getOccurredAt().atOffset(ZoneOffset.UTC))
                .receivedAt(event.getReceivedAt().atOffset(ZoneOffset.UTC))
                .correlationId(event.getCorrelationId())
                .service(event.getService())
                .level(event.getLevel())
                .operation(event.getOperation())
                .entity(event.getEntity())
                .entityId(event.getEntityId())
                .actorType(event.getActorType())
                .actorId(event.getActorId())
                .actorLogin(event.getActorLogin())
                .actorTrust(event.getActorTrust())
                .clientIp(event.getClientIp())
                .httpMethod(event.getHttpMethod())
                .endpoint(event.getEndpoint())
                .outcome(event.getOutcome())
                .httpStatus(event.getHttpStatus())
                .affectedRows(event.getAffectedRows())
                .changedFields(readList(event.getChangedFields()))
                .beforeData(readTree(event.getBeforeData()))
                .afterData(readTree(event.getAfterData()))
                .payloadHash(event.getPayloadHash())
                .metadata(readTree(event.getMetadata()))
                .build();
    }

    private String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private String write(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("No se pudo serializar el payload de auditoría", e);
        }
    }

    private List<String> readList(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, STRING_LIST);
        } catch (JacksonException e) {
            throw new IllegalStateException("changedFields inválido en la base", e);
        }
    }

    private JsonNode readTree(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JacksonException e) {
            throw new IllegalStateException("JSON inválido en la base", e);
        }
    }
}
