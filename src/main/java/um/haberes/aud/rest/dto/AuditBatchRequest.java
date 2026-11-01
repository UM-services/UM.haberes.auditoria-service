package um.haberes.aud.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditBatchRequest {

    public static final int MAX_EVENTS = 500;

    @NotEmpty
    @Size(max = MAX_EVENTS, message = "el lote no puede superar " + MAX_EVENTS + " eventos")
    private List<@Valid AuditEventRequest> events;
}
