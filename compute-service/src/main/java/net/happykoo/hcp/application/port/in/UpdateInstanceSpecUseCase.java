package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.UpdateInstanceSpecCommand;

public interface UpdateInstanceSpecUseCase {

  void updateInstanceSpec(UpdateInstanceSpecCommand command);
}
