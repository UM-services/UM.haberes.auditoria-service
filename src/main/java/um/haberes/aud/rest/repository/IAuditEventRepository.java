package um.haberes.aud.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import um.haberes.aud.rest.model.AuditEvent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface IAuditEventRepository extends JpaRepository<AuditEvent, String>,
        JpaSpecificationExecutor<AuditEvent> {

    List<AuditEvent> findAllByEventIdIn(Collection<String> eventIds);

    List<AuditEvent> findByCorrelationIdOrderByOccurredAtAsc(String correlationId);

    List<AuditEvent> findTop100ByEntityAndEntityIdOrderByOccurredAtDesc(String entity, String entityId);

    List<AuditEvent> findTop100ByActorIdOrderByOccurredAtDesc(Long actorId);

    Page<AuditEvent> findByOccurredAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
