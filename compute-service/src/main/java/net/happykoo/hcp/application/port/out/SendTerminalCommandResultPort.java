package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.terminal.TerminalMessage;

public interface SendTerminalCommandResultPort {

  void send(String sessionId, TerminalMessage message);

  void send(String sessionId, byte[] bytes);

  void close(String sessionId);
}
