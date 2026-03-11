package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;

public interface OpenTerminalSessionUseCase {

  void open(OpenTerminalSessionCommand command);
}
