package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.HandleTerminalControlCommand;

public interface HandleTerminalControlUseCase {

  void handle(HandleTerminalControlCommand handleTerminalControlCommand);
}
