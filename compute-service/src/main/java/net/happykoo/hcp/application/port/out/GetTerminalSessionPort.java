package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.terminal.TerminalSession;

public interface GetTerminalSessionPort {

  TerminalSession findSessionById(String sessionId);
}
