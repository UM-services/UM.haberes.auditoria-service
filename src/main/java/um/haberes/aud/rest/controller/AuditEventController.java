package um.haberes.aud.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.aud.rest.dto.AuditBatchRequest;
import um.haberes.aud.rest.dto.AuditBatchResponse;
import um.haberes.aud.rest.dto.AuditEventRequest;
import um.haberes.aud.rest.dto.AuditEventResponse;
import um.haberes.aud.rest.dto.AuditEventSearchRequest;
import um.haberes.aud.rest.dto.PageResponse;
import um.haberes.aud.rest.service.AuditEventService;

import java.util.List;

@RestController
@RequestMapping("/api/haberes/auditoria/evento")
@RequiredArgsConstructor
public class AuditEventController {

    private final AuditEventService service;

    @PostMapping({"/", ""})
    public ResponseEntity<AuditEventResponse> ingest(@Valid @RequestBody AuditEventRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.ingestOne(request));
    }

    @PostMapping("/lote")
    public ResponseEntity<AuditBatchResponse> ingestBatch(@Valid @RequestBody AuditBatchRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.ingest(request));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<AuditEventResponse> get(@PathVariable String eventId) {
        return ResponseEntity.ok(service.get(eventId));
    }

    @GetMapping("/correlacion/{correlationId}")
    public ResponseEntity<List<AuditEventResponse>> byCorrelation(@PathVariable String correlationId) {
        return ResponseEntity.ok(service.byCorrelation(correlationId));
    }

    @GetMapping("/entidad/{entity}/{entityId}")
    public ResponseEntity<List<AuditEventResponse>> byEntity(@PathVariable String entity,
                                                             @PathVariable String entityId) {
        return ResponseEntity.ok(service.byEntity(entity, entityId));
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<List<AuditEventResponse>> byActor(@PathVariable Long actorId) {
        return ResponseEntity.ok(service.byActor(actorId));
    }

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<PageResponse<AuditEventResponse>> byPeriod(@PathVariable int anho,
                                                                     @PathVariable int mes) {
        return ResponseEntity.ok(service.byPeriod(anho, mes));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse<AuditEventResponse>> search(
            @Valid @RequestBody AuditEventSearchRequest request) {
        return ResponseEntity.ok(service.search(request));
    }
}
