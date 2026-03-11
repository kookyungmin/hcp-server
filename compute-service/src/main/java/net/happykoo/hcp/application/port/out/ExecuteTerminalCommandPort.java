package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.terminal.TerminalSession;

public interface ExecuteTerminalCommandPort {

  void openInstanceTerminal(TerminalSession terminalSession);

  void sendBinary(String sessionId, byte[] bytes);

  void resize(String sessionId, Integer cols, Integer rows);

  void close(String sessionId);
}
