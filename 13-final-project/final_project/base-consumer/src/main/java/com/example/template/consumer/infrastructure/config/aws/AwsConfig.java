package com.example.template.consumer.infrastructure.config.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class AwsConfig {
    private final AwsProperties awsProps;
    private final SqsProperties sqsProps;
    private final AwsSdkProperties sdkProps;

    public AwsConfig(AwsProperties awsProps, SqsProperties sqsProps, AwsSdkProperties sdkProps) {
        this.awsProps = awsProps;
        this.sqsProps = sqsProps;
        this.sdkProps = sdkProps;
    }

    @Bean
    public ClientOverrideConfiguration clientOverrideConfiguration() {
        var retryProps = sdkProps.getRetry();
        var apiCallProps = sdkProps.getApiCall();

        return ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(java.time.Duration.ofMillis(apiCallProps.getAttemptTimeoutMs()))
                .apiCallTimeout(java.time.Duration.ofMillis(apiCallProps.getTimeoutMs()))
                .retryPolicy(RetryPolicy.builder()
                        .numRetries(retryProps.getMaxAttempts())
                        .backoffStrategy(EqualJitterBackoffStrategy.builder()
                                .baseDelay(java.time.Duration.ofMillis(500))
                                .maxBackoffTime(java.time.Duration.ofSeconds(5))
                                .build())
                        .retryCondition(RetryCondition.defaultRetryCondition())
                        .build())
                .build();
    }

    @Bean(destroyMethod = "close")
    public SdkHttpClient sdkHttpClient() {
        return ApacheHttpClient.builder()
                .maxConnections(sdkProps.getHttp().getMaxConnections())
                .connectionTimeout(java.time.Duration.ofMillis(sdkProps.getHttp().getConnectionTimeoutMs()))
                .connectionAcquisitionTimeout(java.time.Duration.ofMillis(sdkProps.getHttp().getConnectionAcquisitionTimeoutMs()))
                .build();
    }

    @Bean(destroyMethod = "close")
    public SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .maxConcurrency(sdkProps.getAsyncHttp().getMaxConcurrency())
                .maxPendingConnectionAcquires(sdkProps.getAsyncHttp().getMaxPendingConnectionAcquires())
                .connectionTimeout(java.time.Duration.ofMillis(sdkProps.getAsyncHttp().getConnectionTimeoutMs()))
                .connectionAcquisitionTimeout(java.time.Duration.ofMillis(sdkProps.getAsyncHttp().getConnectionAcquisitionTimeoutMs()))
                .build();
    }

    @Bean
    public SqsClient sqsClient(SdkHttpClient sdkHttpClient,
                               ClientOverrideConfiguration overrideConfiguration) {
        var builder = SqsClient.builder()
                .httpClient(sdkHttpClient)
                .overrideConfiguration(overrideConfiguration)
                .region(Region.of(awsProps.getRegion()));

        builder.credentialsProvider(resolveCredentials());

        if (isLocal()) {
            var endpoint = awsProps.getLocalstackEndpoint();
            if (endpoint == null || endpoint.isBlank()) {
                endpoint = "http://localhost:4566";
            }
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient(SdkAsyncHttpClient sdkAsyncHttpClient,
                                          ClientOverrideConfiguration overrideConfiguration) {
        var builder = SqsAsyncClient.builder()
                .httpClient(sdkAsyncHttpClient)
                .overrideConfiguration(overrideConfiguration)
                .region(Region.of(awsProps.getRegion()));

        builder.credentialsProvider(resolveCredentials());

        if (isLocal()) {
            var endpoint = awsProps.getLocalstackEndpoint();
            if (endpoint == null || endpoint.isBlank()) {
                endpoint = "http://localhost:4566";
            }
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    private software.amazon.awssdk.auth.credentials.AwsCredentialsProvider resolveCredentials() {
        var accessKey = awsProps.getAccessKeyId();
        var secretKey = awsProps.getSecretKey();

        if (accessKey != null && secretKey != null
                && !accessKey.isBlank() && !secretKey.isBlank()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }

        return DefaultCredentialsProvider.create();
    }

    private boolean isLocal() {
        if (awsProps.getLocalstackEndpoint() != null && !awsProps.getLocalstackEndpoint().isBlank()) {
            return true;
        }

        return sqsProps.getQueues() != null && sqsProps.getQueues().values().stream()
                .map(String::toLowerCase)
                .anyMatch(url ->
                        url.contains("localhost") ||
                                url.contains("127.0.0.1") ||
                                url.contains("4566") ||
                                url.contains("localstack")
                );
    }
}