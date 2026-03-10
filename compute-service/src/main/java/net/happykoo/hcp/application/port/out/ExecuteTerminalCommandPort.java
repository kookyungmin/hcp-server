package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.terminal.TerminalSession;

public interface ExecuteTerminalCommandPort {

  void openInstanceTerminal(TerminalSession terminalSession);

  void sendBinary(String sessionId, byte[] bytes);

  void ping(String sessionId, String message);

  void close(String sessionId);
}
