package net.happykoo.hcp.application;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.LoginUseCase;
import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.application.port.in.result.LoginResult;
import net.happykoo.hcp.application.port.in.result.RefreshAccessTokenResult;
import net.happykoo.hcp.application.port.out.EncryptPasswordPort;
import net.happykoo.hcp.application.port.out.GeneratorTokenPort;
import net.happykoo.hcp.application.port.out.GetTokenPort;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.GetUserPort;
import net.happykoo.hcp.application.port.out.SaveTokenPort;
import net.happykoo.hcp.common.annotation.UseCase;

@UseCase
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

  private final GetUserAccountPort getUserAccountPort;
  private final GetUserPort getUserPort;
  private final EncryptPasswordPort encryptPasswordPort;
  private final GeneratorTokenPort generatorTokenPort;
  private final GetTokenPort getTokenPort;
  private final SaveTokenPort saveTokenPort;

  @Override
  @Transactional
  public LoginResult login(LoginCommand command) {
    //Email로 유저 정보 조회
    var userAccount = getUserAccountPort.getUserAccountByEmail(command.email())
        .orElseThrow(() -> new IllegalStateException("User Account does not exist."));

    //비밀번호 비교
    if (!encryptPasswordPort.matches(command.password(), userAccount.getPasswordHash())) {
      throw new IllegalStateException("Invalid Password.");
    }

    //계정 상태 확인
    var user = getUserPort.getUserById(userAccount.getUserId())
        .orElseThrow(() -> new IllegalStateException("User does not exist."));

    if (!user.isActive()) {
      throw new IllegalStateException("User is not active.");
    }

    //Refresh Token 생성 및 저장
    var refreshToken = generatorTokenPort.createRefreshToken(user);
    saveTokenPort.saveRefreshToken(user.getId(), refreshToken);

    //Access Token 생성
    var accessToken = generatorTokenPort.createAccessToken(user);

    return new LoginResult(accessToken, refreshToken);
  }

  @Override
  public void logout(String refreshToken) {
    saveTokenPort.removeRefreshToken(refreshToken);
  }

  @Override
  public RefreshAccessTokenResult refreshAccessToken(String refreshToken) {
    var userId = getTokenPort.getRefreshTokenPayload(refreshToken).userId();
    if (userId == null) {
      throw new IllegalStateException("Invalid Refresh Token.");
    }
    var user = getUserPort.getUserById(UUID.fromString(userId))
        .orElseThrow(() -> new IllegalStateException("User does not exist."));

    var accessToken = generatorTokenPort.createAccessToken(user);
    return new RefreshAccessTokenResult(accessToken);
  }
}
