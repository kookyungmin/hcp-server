package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.UpdateInstanceTagCommand;

public interface UpdateInstanceTagUseCase {

  void updateInstanceTag(UpdateInstanceTagCommand command);
}
