package net.happykoo.hcp.web.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalRequestRateLimiterGatewayFilterFactory extends
    AbstractGatewayFilterFactory<GlobalRequestRateLimiterGatewayFilterFactory.Config> {

  private final RedisRateLimiter rateLimiter;
  private final KeyResolver globalKeyResolver;
  private final ObjectMapper objectMapper;

  public GlobalRequestRateLimiterGatewayFilterFactory(
      RedisRateLimiter rateLimiter,
      KeyResolver globalKeyResolver,
      ObjectMapper objectMapper
  ) {
    super(Config.class);
    this.globalKeyResolver = globalKeyResolver;
    this.objectMapper = objectMapper;
    this.rateLimiter = rateLimiter;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> globalKeyResolver.resolve(exchange)
        .flatMap(key -> rateLimiter.isAllowed(config.getRouteId(), key))
        .flatMap(result -> {
          if (result.isAllowed()) {
            return chain.filter(exchange);
          }

          return writeTooManyRequests(exchange, result.getHeaders());
        });
  }

  private Mono<? extends Void> writeTooManyRequests(
      ServerWebExchange exchange,
      Map<String, String> headers
  ) {
    var response = exchange.getResponse();

    if (response.isCommitted()) {
      return Mono.empty();
    }

    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    headers.forEach((k, v) -> response.getHeaders().add(k, v));

    byte[] body;
    try {
      body = objectMapper.writeValueAsBytes(Map.of(
          "code", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
          "status", HttpStatus.TOO_MANY_REQUESTS.value(),
          "path", exchange.getRequest().getPath().value(),
          "message", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
          "timestamp", Instant.now().toString()
      ));
    } catch (Exception e) {
      body = String.format("""
                  {"code":"%s","status":%s, "timestamp":%s}}
                  """,
              HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
              HttpStatus.TOO_MANY_REQUESTS.value(),
              Instant.now().toString())
          .getBytes(StandardCharsets.UTF_8);
    }

    DataBuffer buffer = response.bufferFactory().wrap(body);
    return response.writeWith(Mono.just(buffer));
  }

  @Getter
  @Setter
  public static class Config implements HasRouteId {

    private String routeId;

    @Override
    public String getRouteId() {
      return routeId;
    }

    @Override
    public void setRouteId(String routeId) {
      this.routeId = routeId;
    }
  }
}
