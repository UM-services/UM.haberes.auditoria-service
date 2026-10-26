package um.haberes.aud.rest.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import um.haberes.aud.rest.dto.AuditEventSearchRequest;
import um.haberes.aud.rest.model.AuditEvent;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class AuditEventSpecifications {

    private AuditEventSpecifications() {
    }

    public static Specification<AuditEvent> search(AuditEventSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(request.getService())) {
                predicates.add(cb.equal(root.get("service"), request.getService()));
            }
            if (request.getLevel() != null) {
                predicates.add(cb.equal(root.get("level"), request.getLevel()));
            }
            if (request.getOperation() != null) {
                predicates.add(cb.equal(root.get("operation"), request.getOperation()));
            }
            if (request.getOutcome() != null) {
                predicates.add(cb.equal(root.get("outcome"), request.getOutcome()));
            }
            if (request.getActorId() != null) {
                predicates.add(cb.equal(root.get("actorId"), request.getActorId()));
            }
            if (hasText(request.getEntity())) {
                predicates.add(cb.equal(root.get("entity"), request.getEntity()));
            }
            if (hasText(request.getEntityId())) {
                predicates.add(cb.equal(root.get("entityId"), request.getEntityId()));
            }
            if (hasText(request.getCorrelationId())) {
                predicates.add(cb.equal(root.get("correlationId"), request.getCorrelationId()));
            }
            if (request.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), utc(request.getFrom())));
            }
            if (request.getTo() != null) {
                predicates.add(cb.lessThan(root.get("occurredAt"), utc(request.getTo())));
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static java.time.LocalDateTime utc(OffsetDateTime value) {
        return value.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
