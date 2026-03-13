package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;

public interface RegisterInstanceSshKeyUseCase {

  void registerInstanceSshKey(RegisterInstanceSshKeyCommand command);

}
