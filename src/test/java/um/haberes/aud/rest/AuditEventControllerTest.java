package um.haberes.aud.rest;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import um.haberes.aud.rest.dto.AuditBatchRequest;
import um.haberes.aud.rest.dto.AuditEventRequest;
import um.haberes.aud.rest.dto.AuditEventSearchRequest;
import um.haberes.aud.rest.model.ActorTrust;
import um.haberes.aud.rest.model.ActorType;
import um.haberes.aud.rest.model.AuditLevel;
import um.haberes.aud.rest.model.AuditOperation;
import um.haberes.aud.rest.model.AuditOutcome;
import um.haberes.aud.rest.repository.IAuditEventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditEventControllerTest {

    private static final String BASE = "/api/haberes/auditoria/evento";
    private static final String KEY = "test-key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuditEventRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void ingestSinApiKeyDevuelve401() throws Exception {
        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(event("sin-key"))));

        mockMvc.perform(post(BASE + "/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        assertThat(repository.count()).isZero();
    }

    @Test
    void ingestBatchEsIdempotente() throws Exception {
        String eventId = UUID.randomUUID().toString();
        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(event(eventId))));

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accepted").value(1))
                .andExpect(jsonPath("$.duplicates").value(0))
                .andExpect(jsonPath("$.rejected").isEmpty());

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accepted").value(0))
                .andExpect(jsonPath("$.duplicates").value(1));

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void ingestPayloadInvalidoDevuelve400() throws Exception {
        String body = """
                {"events":[{"eventId":"x","occurredAt":"2026-10-26T10:00:00Z",
                "level":"CHANGE","operation":"UPDATE","actorType":"USER",
                "actorTrust":"CLIENT","outcome":"OK","entity":"LegajoBancoEntity"}]}
                """;

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eventoConFechaFuturaSeRechazaSinCortarElLote() throws Exception {
        AuditEventRequest future = event(UUID.randomUUID().toString());
        future.setOccurredAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(30));
        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(future)));

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accepted").value(0))
                .andExpect(jsonPath("$.rejected.length()").value(1));

        assertThat(repository.count()).isZero();
    }

    @Test
    void consultaPorIdCorrelacionEntidadActorYPeriodo() throws Exception {
        String correlationId = UUID.randomUUID().toString();
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        AuditEventRequest first = event(firstId);
        first.setCorrelationId(correlationId);
        AuditEventRequest second = event(secondId);
        second.setCorrelationId(correlationId);

        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(first, second)));
        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        mockMvc.perform(get(BASE + "/{id}", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(firstId))
                .andExpect(jsonPath("$.actorId").value(123))
                .andExpect(jsonPath("$.beforeData.cbu").value("****6789"));

        mockMvc.perform(get(BASE + "/correlacion/{id}", correlationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get(BASE + "/entidad/{entity}/{id}", "LegajoBancoEntity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get(BASE + "/actor/{id}", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockMvc.perform(get(BASE + "/periodo/{anho}/{mes}", now.getYear(), now.getMonthValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        AuditEventSearchRequest search = AuditEventSearchRequest.builder()
                .entity("LegajoBancoEntity")
                .actorId(123L)
                .build();
        mockMvc.perform(post(BASE + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(search)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void eventoSummaryBulkSeAceptaYSeBuscaPorNivelYOperacion() throws Exception {
        String eventId = UUID.randomUUID().toString();
        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(bulkEvent(eventId))));

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accepted").value(1))
                .andExpect(jsonPath("$.rejected").isEmpty());

        mockMvc.perform(get(BASE + "/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value("SUMMARY"))
                .andExpect(jsonPath("$.operation").value("BULK"))
                .andExpect(jsonPath("$.affectedRows").value(32))
                .andExpect(jsonPath("$.metadata.counts.INSERT").value(32));

        AuditEventSearchRequest search = AuditEventSearchRequest.builder()
                .level(AuditLevel.SUMMARY)
                .operation(AuditOperation.BULK)
                .build();
        mockMvc.perform(post(BASE + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(search)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].entity").value("ItemEntity"));
    }

    @Test
    void bulkSinAffectedRowsSeRechazaSinCortarElLote() throws Exception {
        AuditEventRequest invalid = bulkEvent(UUID.randomUUID().toString());
        invalid.setAffectedRows(null);
        String body = objectMapper.writeValueAsString(new AuditBatchRequest(List.of(invalid)));

        mockMvc.perform(post(BASE + "/lote")
                        .header("X-API-Key", KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accepted").value(0))
                .andExpect(jsonPath("$.rejected.length()").value(1))
                .andExpect(jsonPath("$.rejected[0].reason")
                        .value("affectedRows es obligatorio cuando operation=BULK"));
    }

    @Test
    void eventoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get(BASE + "/{id}", "no-existe"))
                .andExpect(status().isNotFound());
    }

    private AuditEventRequest event(String eventId) {
        return AuditEventRequest.builder()
                .eventId(eventId)
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1))
                .correlationId(UUID.randomUUID().toString())
                .service("haberes-core")
                .level(AuditLevel.CHANGE)
                .operation(AuditOperation.UPDATE)
                .entity("LegajoBancoEntity")
                .entityId("1")
                .actorType(ActorType.USER)
                .actorId(123L)
                .actorLogin("dquinteros")
                .actorTrust(ActorTrust.CLIENT)
                .clientIp("10.147.20.77")
                .httpMethod("PUT")
                .endpoint("/api/haberes/core/legajobanco/1")
                .outcome(AuditOutcome.OK)
                .httpStatus(200)
                .changedFields(List.of("cbu"))
                .beforeData(Map.of("cbu", "****6789"))
                .afterData(Map.of("cbu", "****4321"))
                .build();
    }

    private AuditEventRequest bulkEvent(String eventId) {
        return AuditEventRequest.builder()
                .eventId(eventId)
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1))
                .correlationId(UUID.randomUUID().toString())
                .service("haberes-core")
                .level(AuditLevel.SUMMARY)
                .operation(AuditOperation.BULK)
                .entity("ItemEntity")
                .actorType(ActorType.SYSTEM)
                .actorTrust(ActorTrust.SYSTEM)
                .outcome(AuditOutcome.OK)
                .affectedRows(32)
                .metadata(Map.of(
                        "table", "item",
                        "counts", Map.of("INSERT", 32),
                        "label", "recalculo-legajo"))
                .build();
    }
}
