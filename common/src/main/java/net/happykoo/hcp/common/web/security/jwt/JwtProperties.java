package net.happykoo.hcp.common.web.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.access-token")
@NoArgsConstructor
@Getter
@Setter
public class JwtProperties {

  private String secretKey;
  private int expireTime;
}
