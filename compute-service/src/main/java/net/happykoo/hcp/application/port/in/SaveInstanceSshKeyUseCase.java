package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;

public interface SaveInstanceSshKeyUseCase {

  void saveInstanceSshKey(RegisterInstanceSshKeyCommand command);
}
