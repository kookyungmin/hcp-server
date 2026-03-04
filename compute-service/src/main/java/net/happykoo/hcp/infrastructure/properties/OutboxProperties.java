package net.happykoo.hcp.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "outbox")
public record OutboxProperties(
    int pollIntervalMs,
    int batchSize,
    int kafkaAckTimeoutMs
) {

}
