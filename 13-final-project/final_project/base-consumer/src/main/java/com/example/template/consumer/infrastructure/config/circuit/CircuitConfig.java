package com.example.template.consumer.infrastructure.config.circuit;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CircuitConfig {

    @Bean(destroyMethod = "close")
    public CloseableHttpClient externalServiceHttpClient(ExternalServiceProperties props) {
        return HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(props.getPool().getMaxConnTotal())
                        .setMaxConnPerRoute(props.getPool().getMaxConnPerRoute())
                        .setDefaultConnectionConfig(ConnectionConfig.custom()
                                .setConnectTimeout(Timeout.ofMilliseconds(props.getHttp().getConnectTimeoutMs()))
                                .setSocketTimeout(Timeout.ofMilliseconds(props.getHttp().getReadTimeoutMs()))
                                .setValidateAfterInactivity(TimeValue.ofSeconds(props.getPool().getKeepAliveSeconds()))
                                .build())
                        .build())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMinutes(5))
                .build();
    }

    @Bean
    public RestTemplate externalServiceRestTemplate(CloseableHttpClient externalServiceHttpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(externalServiceHttpClient);
        return new RestTemplate(factory);
    }
}