package com.example.template.api.infrastructure.adapter.in.rest.filters;

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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String HEALTH_PATH = "/health";

    private final String expectedKey;

    public ApiKeyFilter(@Value("${app.api-key:}") String expectedKey) {
        this.expectedKey = expectedKey == null || expectedKey.isBlank() ? null : expectedKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (expectedKey == null || isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(API_KEY_HEADER);
        if (expectedKey.equals(providedKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isPublicPath(String uri) {
        return uri != null && (uri.equals(HEALTH_PATH) || uri.startsWith("/health") || uri.startsWith("/actuator"));
    }
}