package um.haberes.aud.rest.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

/**
 * Autentica la ingesta de eventos con una API key por emisor.
 *
 * <p>El endpoint de lectura queda abierto en la red interna (JWT llegará después);
 * la escritura es la que no puede quedar anónima: si cualquiera puede insertar, la
 * auditoría deja de ser confiable.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuditApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-Key";

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/haberes/auditoria/evento",
            "/api/haberes/auditoria/evento/",
            "/api/haberes/auditoria/evento/lote");

    private final String apiKey;

    public AuditApiKeyFilter(@Value("${app.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return !PROTECTED_PATHS.contains(pathWithoutContext(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (apiKey == null || apiKey.isBlank()) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API key no configurada en el servicio de auditoría");
            return;
        }

        String provided = request.getHeader(HEADER);
        if (provided == null || !MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                provided.getBytes(StandardCharsets.UTF_8))) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Falta o es inválida la cabecera " + HEADER);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String pathWithoutContext(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        return context == null || context.isEmpty() ? uri : uri.substring(context.length());
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"error\":\"" + (status == 401 ? "Unauthorized" : "Service Unavailable")
                + "\",\"message\":\"" + message + "\"}");
    }
}
