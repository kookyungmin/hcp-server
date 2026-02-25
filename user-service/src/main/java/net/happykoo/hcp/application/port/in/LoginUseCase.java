package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.application.port.in.result.LoginResult;

public interface LoginUseCase {

  LoginResult login(LoginCommand command);
}
