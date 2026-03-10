package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.terminal.TerminalSession;

public interface SaveTerminalSessionPort {

  void save(TerminalSession terminalSession);

  void remove(String sessionId);
}
