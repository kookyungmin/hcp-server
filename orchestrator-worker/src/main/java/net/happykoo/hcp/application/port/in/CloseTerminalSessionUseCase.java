package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;

public interface CloseTerminalSessionUseCase {

  void close(CloseTerminalSessionCommand command);
}
