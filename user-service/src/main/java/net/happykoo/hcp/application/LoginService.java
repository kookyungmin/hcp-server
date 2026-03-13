package net.happykoo.hcp.application;

import static net.happykoo.hcp.adapter.out.persistence.redis.CacheNames.USER_PROFILE;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.LoginUseCase;
import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.application.port.in.result.GetLoginUserInfo;
import net.happykoo.hcp.application.port.in.result.LoginResult;
import net.happykoo.hcp.application.port.in.result.RefreshAccessTokenResult;
import net.happykoo.hcp.application.port.out.EncryptPasswordPort;
import net.happykoo.hcp.application.port.out.GeneratorTokenPort;
import net.happykoo.hcp.application.port.out.GetTokenPort;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.GetUserPort;
import net.happykoo.hcp.application.port.out.SaveTokenPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.common.web.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

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
        .orElseThrow(() -> new ResourceNotFoundException("사용자 계정이 존재하지 않습니다."));

    //비밀번호 비교
    if (!encryptPasswordPort.matches(command.password(), userAccount.getPasswordHash())) {
      throw new IllegalStateException("비밀번호가 올바르지 않습니다.");
    }

    //계정 상태 확인
    var user = getUserPort.getUserById(userAccount.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

    if (!user.isActive()) {
      throw new IllegalStateException("활성화된 사용자가 아닙니다.");
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
      throw new IllegalStateException("로그인이 필요합니다.");
    }
    var user = getUserPort.getUserById(UUID.fromString(userId))
        .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

    var accessToken = generatorTokenPort.createAccessToken(user);
    return new RefreshAccessTokenResult(accessToken);
  }

  //TODO: CacheEvict 설정
  @Override
  @Cacheable(cacheManager = "userProfileCacheManager", cacheNames = USER_PROFILE, key = "#userId")
  public GetLoginUserInfo getLoginUserInfo(UUID userId) {
    var userAccountProfile = getUserAccountPort.getUserAccountViewById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("사용자 계정이 존재하지 않습니다."));

    var userProfile = getUserPort.getUserProfileById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

    return new GetLoginUserInfo(
        userId,
        userProfile.displayName(),
        userAccountProfile.email(),
        userAccountProfile.passwordChangedAt()
    );
  }
}
