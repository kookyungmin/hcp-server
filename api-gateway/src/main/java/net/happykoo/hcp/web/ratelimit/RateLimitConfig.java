package net.happykoo.hcp.web.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

  private final static String X_USER_ID = "X-User-Id";
  private final static String RATE_LIMIT_KEY_PREFIX = "rl:";
  private final static String RATE_LIMIT_USER_KEY_PREFIX = RATE_LIMIT_KEY_PREFIX + "user:";
  private final static String RATE_LIMIT_IP_KEY_PREFIX = RATE_LIMIT_KEY_PREFIX + "anonymous:";

  @Bean
  public KeyResolver globalKeyResolver() {
    return exchange -> {
      String userId = exchange.getRequest().getHeaders().getFirst(X_USER_ID);

      if (userId != null && !userId.isBlank()) {
        return Mono.just(RATE_LIMIT_USER_KEY_PREFIX + userId);
      }

      return Mono.just(
          RATE_LIMIT_IP_KEY_PREFIX + resolveClientIp(exchange));
    };
  }

  private String resolveClientIp(ServerWebExchange exchange) {
    String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
      return xff.split(",")[0].trim();
    }

    var remoteAddress = exchange.getRequest().getRemoteAddress();
    if (remoteAddress == null || remoteAddress.getAddress() == null) {
      return "unknown";
    }
    return remoteAddress.getAddress().getHostAddress();
  }
}
