package net.happykoo.hcp.adapter.out.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.domain.terminal.TerminalSession;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrchestratorWorkerWebSocketAdapter implements ExecuteTerminalCommandPort {

  @Override
  public void openInstanceTerminal(TerminalSession terminalSession) {
    log.info("openInstanceTerminal : {}", terminalSession);
  }

  @Override
  public void sendBinary(String sessionId, byte[] bytes) {
    log.info("sendBinary : {}", sessionId);
  }

  @Override
  public void close(String sessionId) {
    log.info("close : {}", sessionId);
  }

  @Override
  public void resizeTerminal(String sessionId, Integer cols, Integer rows) {
    //TODO: 후에 구현
  }

  @Override
  public void ping(String sessionId, String message) {
    //TODO: 후에 구현
  }
}
