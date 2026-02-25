package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.UserRegisterCommand;

public interface RegisterUserUseCase {

  void registerMasterUser(UserRegisterCommand command);
}
