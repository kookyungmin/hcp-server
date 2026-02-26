package net.happykoo.hcp.web.auth.filter.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.access-token")
@Getter
@Setter
public class AccessTokenProperties {

  private String secretKey;
}
