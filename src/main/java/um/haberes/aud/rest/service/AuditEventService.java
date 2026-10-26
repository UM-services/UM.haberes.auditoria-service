package um.haberes.aud.rest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.haberes.aud.rest.dto.AuditBatchRequest;
import um.haberes.aud.rest.dto.AuditBatchResponse;
import um.haberes.aud.rest.dto.AuditEventRequest;
import um.haberes.aud.rest.dto.AuditEventResponse;
import um.haberes.aud.rest.dto.AuditEventSearchRequest;
import um.haberes.aud.rest.dto.PageResponse;
import um.haberes.aud.rest.exception.AuditEventNotFoundException;
import um.haberes.aud.rest.model.AuditEvent;
import um.haberes.aud.rest.model.AuditLevel;
import um.haberes.aud.rest.model.AuditOperation;
import um.haberes.aud.rest.repository.AuditEventSpecifications;
import um.haberes.aud.rest.repository.IAuditEventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuditEventService {

    static final int MAX_PERIOD_ROWS = 200;
    static final int MAX_SKEW_MINUTES = 5;

    private final IAuditEventRepository repository;
    private final AuditEventMapper mapper;

    @Transactional
    public AuditBatchResponse ingest(AuditBatchRequest request) {
        List<AuditEventRequest> events = request.getEvents();
        LocalDateTime now = nowUtc();

        List<String> eventIds = events.stream().map(AuditEventRequest::getEventId).toList();
        Set<String> alreadyStored = new HashSet<>();
        repository.findAllByEventIdIn(eventIds).forEach(event -> alreadyStored.add(event.getEventId()));

        List<AuditEvent> toSave = new ArrayList<>();
        List<AuditBatchResponse.RejectedEvent> rejected = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int duplicates = 0;

        for (int index = 0; index < events.size(); index++) {
            AuditEventRequest event = events.get(index);
            if (!seen.add(event.getEventId()) || alreadyStored.contains(event.getEventId())) {
                duplicates++;
                continue;
            }
            String reason = invalidReason(event, now);
            if (reason != null) {
                rejected.add(new AuditBatchResponse.RejectedEvent(index, reason));
                continue;
            }
            toSave.add(mapper.toEntity(event, now));
        }

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
        return new AuditBatchResponse(toSave.size(), duplicates, rejected);
    }

    @Transactional
    public AuditEventResponse ingestOne(AuditEventRequest request) {
        AuditBatchResponse result = ingest(new AuditBatchRequest(List.of(request)));
        if (!result.rejected().isEmpty()) {
            throw new IllegalArgumentException(result.rejected().get(0).reason());
        }
        return get(request.getEventId());
    }

    @Transactional(readOnly = true)
    public AuditEventResponse get(String eventId) {
        return repository.findById(eventId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AuditEventNotFoundException(eventId));
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> byCorrelation(String correlationId) {
        return repository.findByCorrelationIdOrderByOccurredAtAsc(correlationId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> byEntity(String entity, String entityId) {
        return repository.findTop100ByEntityAndEntityIdOrderByOccurredAtDesc(entity, entityId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> byActor(Long actorId) {
        return repository.findTop100ByActorIdOrderByOccurredAtDesc(actorId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> byPeriod(int anho, int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("mes debe estar entre 1 y 12");
        }
        LocalDateTime from = LocalDate.of(anho, mes, 1).atStartOfDay();
        LocalDateTime to = from.plusMonths(1);
        Pageable pageable = PageRequest.of(0, MAX_PERIOD_ROWS,
                Sort.by(Sort.Direction.DESC, "occurredAt"));
        Page<AuditEvent> page = repository.findByOccurredAtBetween(from, to, pageable);
        return PageResponse.from(page, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditEventResponse> search(AuditEventSearchRequest request) {
        if (request.getFrom() != null && request.getTo() != null
                && request.getFrom().isAfter(request.getTo())) {
            throw new IllegalArgumentException("from no puede ser posterior a to");
        }
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = request.getSize() == null ? 50 : Math.min(request.getSize(), 200);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));
        Page<AuditEvent> result = repository.findAll(AuditEventSpecifications.search(request), pageable);
        return PageResponse.from(result, mapper::toResponse);
    }

    private String invalidReason(AuditEventRequest event, LocalDateTime now) {
        if (event.getOccurredAt().isAfter(now.atOffset(ZoneOffset.UTC).plusMinutes(MAX_SKEW_MINUTES))) {
            return "occurredAt no puede estar más de " + MAX_SKEW_MINUTES + " minutos en el futuro";
        }
        if ((event.getLevel() == AuditLevel.CHANGE || event.getLevel() == AuditLevel.SUMMARY)
                && (event.getEntity() == null || event.getEntity().isBlank())) {
            return "entity es obligatorio cuando level=" + event.getLevel();
        }
        if (event.getOperation() == AuditOperation.BULK
                && (event.getAffectedRows() == null || event.getAffectedRows() <= 0)) {
            return "affectedRows es obligatorio cuando operation=BULK";
        }
        return null;
    }

    private LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
