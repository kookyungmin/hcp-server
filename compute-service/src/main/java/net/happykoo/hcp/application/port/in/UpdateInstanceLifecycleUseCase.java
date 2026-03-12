package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;

public interface UpdateInstanceLifecycleUseCase {

  void stopInstance(UpdateInstanceLifecycleCommand command);

  void restartInstance(UpdateInstanceLifecycleCommand command);

  void terminateInstance(UpdateInstanceLifecycleCommand command);
}
