package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.application.port.in.result.LoginResult;
import net.happykoo.hcp.application.port.in.result.RefreshAccessTokenResult;

public interface LoginUseCase {

  LoginResult login(LoginCommand command);

  void logout(String refreshToken);

  RefreshAccessTokenResult refreshAccessToken(String refreshToken);
}
