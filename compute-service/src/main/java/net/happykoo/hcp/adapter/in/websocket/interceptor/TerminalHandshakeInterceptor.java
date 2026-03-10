package net.happykoo.hcp.adapter.in.websocket.interceptor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TerminalHandshakeInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response,
      @NonNull WebSocketHandler wsHandler,
      @NonNull Map<String, Object> attributes
  ) throws Exception {
    //web socket handler 에서 사용할 수 있게 userId 와 roles 셋팅
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()) {
      response.setStatusCode(UNAUTHORIZED);
      return false;
    }

    String instanceId = UriComponentsBuilder.fromUri(request.getURI())
        .build()
        .getQueryParams()
        .getFirst("instanceId");

    if (StringUtils.isEmpty(instanceId)) {
      response.setStatusCode(BAD_REQUEST);
      return false;
    }

    attributes.put("userId", authentication.getName());
    attributes.put("instanceId", instanceId);
    attributes.put("roles", authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList());
    return true;
  }

  @Override
  public void afterHandshake(
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response,
      @NonNull WebSocketHandler wsHandler,
      @Nullable Exception exception
  ) {

  }
}
