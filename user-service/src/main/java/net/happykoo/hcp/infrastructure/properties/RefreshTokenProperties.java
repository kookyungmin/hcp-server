package net.happykoo.hcp.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.refresh-token")
@Getter
@Setter
public class RefreshTokenProperties {

  private int expireTime;
  private String prefix;
  private boolean secure;
}
