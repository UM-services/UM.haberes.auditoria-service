package um.haberes.aud.rest.dto;

import java.util.List;

public record AuditBatchResponse(int accepted, int duplicates, List<RejectedEvent> rejected) {

    public record RejectedEvent(int index, String reason) {
    }
}
