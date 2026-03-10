package net.happykoo.hcp.adapter.in.websocket.session;

import java.util.List;
import java.util.UUID;
import org.springframework.web.socket.WebSocketSession;

public record TerminalWebSocketSessionAttributes(
    UUID instanceId,
    UUID userId,
    List<String> roles
) {

  @SuppressWarnings("unchecked")
  public static TerminalWebSocketSessionAttributes from(WebSocketSession session) {
    return new TerminalWebSocketSessionAttributes(
        UUID.fromString((String) session.getAttributes().get("instanceId")),
        UUID.fromString((String) session.getAttributes().get("userId")),
        (List<String>) session.getAttributes().getOrDefault("roles", List.of())
    );
  }
}
