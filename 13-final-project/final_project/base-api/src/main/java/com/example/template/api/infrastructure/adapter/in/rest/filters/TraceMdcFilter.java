package com.example.template.api.infrastructure.adapter.in.rest.filters;

import com.example.template.api.infrastructure.logging.CorrelationIdSupport;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceMdcFilter implements Filter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean removeCorrelationId = false;
        try {
            if (request instanceof HttpServletRequest httpRequest) {
                var headerValue = httpRequest.getHeader(CORRELATION_ID_HEADER);
                removeCorrelationId = CorrelationIdSupport.ensureFrom(headerValue);
            }

            var traceId = MDC.get("traceId");
            var spanId = MDC.get("spanId");

            if (traceId != null) {
                MDC.put("trace_id", traceId);
            }
            if (spanId != null) {
                MDC.put("trace_msg", spanId);
            }

            chain.doFilter(request, response);

        } finally {
            CorrelationIdSupport.clearIfAdded(removeCorrelationId);
            MDC.remove("trace_id");
            MDC.remove("trace_msg");
        }
    }
}