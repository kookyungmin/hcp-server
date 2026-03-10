package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;

public interface HandleTerminalBinaryInputUseCase {

  void handle(HandleTerminalBinaryInputCommand handleTerminalBinaryInputCommand);
}
