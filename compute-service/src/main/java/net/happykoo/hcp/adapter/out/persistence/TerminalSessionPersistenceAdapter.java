package net.happykoo.hcp.adapter.out.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.happykoo.hcp.application.port.out.GetTerminalSessionPort;
import net.happykoo.hcp.application.port.out.SaveTerminalSessionPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.terminal.TerminalSession;

@PersistenceAdapter
public class TerminalSessionPersistenceAdapter implements SaveTerminalSessionPort,
    GetTerminalSessionPort {

  private final Map<String, TerminalSession> storage = new ConcurrentHashMap<>();

  @Override
  public TerminalSession findSessionById(String sessionId) {
    return storage.get(sessionId);
  }

  @Override
  public void save(TerminalSession terminalSession) {
    storage.put(terminalSession.getSessionId(), terminalSession);
  }

  @Override
  public void remove(String sessionId) {
    storage.remove(sessionId);
  }
}
