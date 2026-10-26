package um.haberes.aud.rest.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.StringJoiner;

/**
 * Hash canónico del contenido del evento. Permite detectar alteraciones posteriores
 * de una fila (no es una cadena de hashes entre filas).
 */
@Component
public class AuditPayloadHasher {

    public String hash(String eventId,
                       LocalDateTime occurredAt,
                       String correlationId,
                       String service,
                       String level,
                       String operation,
                       String entity,
                       String entityId,
                       String actorType,
                       Long actorId,
                       String actorLogin,
                       String actorTrust,
                       String clientIp,
                       String httpMethod,
                       String endpoint,
                       String outcome,
                       Integer httpStatus,
                       Integer affectedRows,
                       String changedFieldsJson,
                       String beforeJson,
                       String afterJson) {
        StringJoiner canonical = new StringJoiner("\u001f");
        canonical.add(value(eventId));
        canonical.add(value(occurredAt));
        canonical.add(value(correlationId));
        canonical.add(value(service));
        canonical.add(value(level));
        canonical.add(value(operation));
        canonical.add(value(entity));
        canonical.add(value(entityId));
        canonical.add(value(actorType));
        canonical.add(value(actorId));
        canonical.add(value(actorLogin));
        canonical.add(value(actorTrust));
        canonical.add(value(clientIp));
        canonical.add(value(httpMethod));
        canonical.add(value(endpoint));
        canonical.add(value(outcome));
        canonical.add(value(httpStatus));
        canonical.add(value(affectedRows));
        canonical.add(value(changedFieldsJson));
        canonical.add(value(beforeJson));
        canonical.add(value(afterJson));
        return sha256(canonical.toString());
    }

    private String value(Object value) {
        return value == null ? "" : value.toString();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
