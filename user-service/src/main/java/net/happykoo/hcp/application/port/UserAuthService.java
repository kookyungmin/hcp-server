package net.happykoo.hcp.application.port;

import net.happykoo.hcp.application.port.in.LoginUseCase;
import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.application.port.in.result.LoginResult;
import net.happykoo.hcp.common.annotation.UseCase;

@UseCase
public class UserAuthService implements LoginUseCase {

  @Override
  public LoginResult login(LoginCommand command) {
    //Email로 유저 정보 조회

    //계정 상태 확인

    //비밀번호 비교

    //access token + refresh token 생성
    return null;
  }
}
