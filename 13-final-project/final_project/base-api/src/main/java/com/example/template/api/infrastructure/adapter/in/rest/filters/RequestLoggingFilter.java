package com.example.template.api.infrastructure.adapter.in.rest.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = normalizePath(request.getRequestURI(), request.getContextPath());
        if (isHealthPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            if (log.isInfoEnabled()) {
                String rawUserAgent = request.getHeader("User-Agent");
                String safeUserAgent = rawUserAgent != null
                        ? rawUserAgent.replaceAll("[\\r\\n]", " ").strip()
                        : "none";
                log.info(
                        "method={} path={} status={} ip={} duration={}ms userAgent={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        request.getRemoteAddr(),
                        duration,
                        safeUserAgent
                );
            }
        }
    }

    private String normalizePath(String requestUri, String contextPath) {
        if (requestUri == null) {
            return null;
        }

        String normalized = requestUri;
        if (contextPath != null && !contextPath.isBlank() && normalized.startsWith(contextPath)) {
            normalized = normalized.substring(contextPath.length());
        }

        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        return normalized;
    }

    private boolean isHealthPath(String path) {
        if (path == null) {
            return false;
        }

        return path.equals("health")
                || path.equals("health/live")
                || path.equals("health/ready")
                || path.equals("actuator/health")
                || path.startsWith("actuator/health/")
                || path.equals("actuator/info")
                || path.equals("actuator/metrics")
                || path.startsWith("actuator/metrics/")
                || path.equals("actuator/prometheus");
    }
}