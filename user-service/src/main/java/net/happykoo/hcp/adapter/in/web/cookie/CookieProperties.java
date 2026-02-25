package net.happykoo.hcp.adapter.in.web.cookie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.refresh-token")
@Getter
@Setter
public class CookieProperties {

  private int expireTime;
  private String cookieName;
  private boolean secure;
}
