package net.happykoo.hcp.adapter.in.websocket.config;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.websocket.handler.TerminalWebSocketHandler;
import net.happykoo.hcp.adapter.in.websocket.interceptor.TerminalHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final TerminalHandshakeInterceptor terminalHandshakeInterceptor;
  private final TerminalWebSocketHandler terminalWebSocketHandler;

  @Override
  public void registerWebSocketHandlers(
      @NonNull WebSocketHandlerRegistry registry
  ) {
    registry.addHandler(terminalWebSocketHandler, "/ws/instances/terminal")
        .addInterceptors(terminalHandshakeInterceptor)
        .setAllowedOrigins("*");
  }
}
