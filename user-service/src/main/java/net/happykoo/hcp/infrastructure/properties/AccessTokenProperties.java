package net.happykoo.hcp.infrastructure.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.access-token")
@NoArgsConstructor
@Getter
@Setter
public class AccessTokenProperties {

  private String secretKey;
  private int expireTime;
}
