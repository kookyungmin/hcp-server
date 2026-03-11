package net.happykoo.hcp.adapter.in.websocket.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

@Component
public class TerminalWebSocketSessionRegistry {

  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  public void register(String sessionId, WebSocketSession session) {
    WebSocketSession decorated = new ConcurrentWebSocketSessionDecorator(
        session,
        10_000, //10초
        1024 * 1024 //1MB
    );
    sessions.put(sessionId, decorated);
  }

  public WebSocketSession getSession(String sessionId) {
    return sessions.get(sessionId);
  }

  public String getSessionId(WebSocketSession session) {
    return sessions.entrySet().stream()
        .filter(entry -> ((ConcurrentWebSocketSessionDecorator) entry.getValue()).getDelegate()
            .equals(session))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
  }

  public void remove(String sessionId) {
    sessions.remove(sessionId);
  }

}
