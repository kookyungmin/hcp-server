package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.domain.terminal.TerminalMessage;

public interface ResizeTerminalSessionUseCase {

  void resize(
      TerminalMessage message
  );

}
