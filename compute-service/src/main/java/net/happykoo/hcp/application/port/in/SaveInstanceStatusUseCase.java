package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.SaveInstanceStatusCommand;

public interface SaveInstanceStatusUseCase {

  void saveInstanceStatus(SaveInstanceStatusCommand command);
}
