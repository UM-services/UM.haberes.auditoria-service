package um.haberes.aud.rest.exception;

public class AuditEventNotFoundException extends RuntimeException {

    public AuditEventNotFoundException(String eventId) {
        super("No existe el evento de auditoría " + eventId);
    }
}
