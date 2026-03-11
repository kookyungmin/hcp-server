package net.happykoo.hcp.adapter.out.orchestrator.openapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class K8sTerminalSessionRegistry {

  private final Map<String, K8sTerminalSession> sessions = new ConcurrentHashMap<>();

  public void register(
      String sessionId,
      K8sTerminalSession session
  ) {
    sessions.put(sessionId, session);
  }

  public K8sTerminalSession get(String sessionId) {
    return sessions.get(sessionId);
  }

  public K8sTerminalSession remove(String sessionId) {
    return sessions.remove(sessionId);
  }
}
