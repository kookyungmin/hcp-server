package net.happykoo.hcp.web.auth.filter.jwt;

import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.common.utils.jwt.JwtProperties;
import net.happykoo.hcp.common.utils.jwt.JwtProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@EnableConfigurationProperties(AccessTokenProperties.class)
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

  private static final String BEARER = "Bearer ";
  private static final String X_USER_ID = "X-USER-ID";
  private static final String X_ROLES = "X-ROLES";
  private final JwtProvider jwtProvider;


  public JwtAuthenticationFilter(
      AccessTokenProperties accessTokenProperties
  ) {
    this.jwtProvider = new JwtProvider(new JwtProperties(accessTokenProperties.getSecretKey()));
  }

  @Override
  public Mono<Void> filter(
      ServerWebExchange exchange,
      GatewayFilterChain chain
  ) {
    var reqBuilder = exchange.getRequest().mutate()
        // 클라이언트가 임의로 넣은 값 무조건 제거(위조 방지)
        .headers(h -> {
          h.remove(X_USER_ID);
          h.remove(X_ROLES);
        });

    var auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (auth == null || !auth.startsWith(BEARER)) {
      return chain.filter(exchange.mutate().request(reqBuilder.build()).build());
    }

    var accessToken = auth.substring("Bearer ".length()).trim();
    if (accessToken.isEmpty()) {
      return chain.filter(exchange.mutate().request(reqBuilder.build()).build());
    }

    try {
      var userId = jwtProvider.parseToken(accessToken, "userId");
      if (StringUtils.isNotBlank(userId)) {
        reqBuilder.header(X_USER_ID, userId);
      }

      var scopes = jwtProvider.parseToken(accessToken, "scopes");
      if (StringUtils.isNotBlank(userId)) {
        reqBuilder.header(X_ROLES, scopes);
      }
    } catch (Exception e) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    return chain.filter(exchange.mutate().request(reqBuilder.build()).build());
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
