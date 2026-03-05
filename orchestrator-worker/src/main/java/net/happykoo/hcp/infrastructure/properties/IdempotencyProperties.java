package net.happykoo.hcp.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "idempotency")
@Getter
@Setter
public class IdempotencyProperties {

  private long processingTtlSeconds;

}