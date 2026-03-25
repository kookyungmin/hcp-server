package net.happykoo.hcp.application;

import static net.happykoo.hcp.adapter.out.persistence.redis.CacheNames.USER_PROFILE;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
      log.warn("로그인에 실패했습니다. 비밀번호가 일치하지 않습니다. email={}", command.email());
      throw new IllegalStateException("비밀번호가 올바르지 않습니다.");
    }

    //계정 상태 확인
    var user = getUserPort.getUserById(userAccount.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

    if (!user.isActive()) {
      log.warn("로그인에 실패했습니다. 비활성 사용자입니다. userId={}", user.getId());
      throw new IllegalStateException("활성화된 사용자가 아닙니다.");
    }

    //Refresh Token 생성 및 저장
    var refreshToken = generatorTokenPort.createRefreshToken(user);
    saveTokenPort.saveRefreshToken(user.getId(), refreshToken);

    //Access Token 생성
    var accessToken = generatorTokenPort.createAccessToken(user);
    log.info("로그인에 성공했습니다. userId={}", user.getId());

    return new LoginResult(accessToken, refreshToken);
  }

  @Override
  public void logout(String refreshToken) {
    log.info("로그아웃을 처리합니다.");
    saveTokenPort.removeRefreshToken(refreshToken);
  }

  @Override
  public RefreshAccessTokenResult refreshAccessToken(String refreshToken) {
    var userId = getTokenPort.getRefreshTokenPayload(refreshToken).userId();
    if (userId == null) {
      log.warn("액세스 토큰 재발급에 실패했습니다. 리프레시 토큰이 유효하지 않습니다.");
      throw new IllegalStateException("로그인이 필요합니다.");
    }
    var user = getUserPort.getUserById(UUID.fromString(userId))
        .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));

    var accessToken = generatorTokenPort.createAccessToken(user);
    log.info("액세스 토큰 재발급에 성공했습니다. userId={}", userId);
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
