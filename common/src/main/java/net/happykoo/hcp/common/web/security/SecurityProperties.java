package net.happykoo.hcp.common.web.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {

  private List<String> allowedApiPaths;
  private List<String> allowedOrigins;

}
