package net.happykoo.hcp.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import net.happykoo.hcp.application.port.out.data.RefreshTokenPayload;
import net.happykoo.hcp.application.port.out.data.UserAccountView;
import net.happykoo.hcp.application.port.out.data.UserProfile;
import net.happykoo.hcp.common.web.exception.ResourceNotFoundException;
import net.happykoo.hcp.domain.User;
import net.happykoo.hcp.domain.UserAccount;
import net.happykoo.hcp.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock
  private GetUserAccountPort getUserAccountPort;
  @Mock
  private GetUserPort getUserPort;
  @Mock
  private EncryptPasswordPort encryptPasswordPort;
  @Mock
  private GeneratorTokenPort generatorTokenPort;
  @Mock
  private GetTokenPort getTokenPort;
  @Mock
  private SaveTokenPort saveTokenPort;

  @InjectMocks
  private LoginService loginService;

  @Test
  @DisplayName("login() :: 정상 로그인 시 access/refresh token 을 반환하고 refresh token 을 저장")
  void loginTest1() {
    UUID userId = UUID.randomUUID();
    UserAccount account = new UserAccount(userId, "happykoo@example.com", "encoded-password");
    User user = User.createMasterUser(userId, "happykoo", UserStatus.ACTIVE);

    when(getUserAccountPort.getUserAccountByEmail("happykoo@example.com"))
        .thenReturn(Optional.of(account));
    when(encryptPasswordPort.matches("plain-password", "encoded-password")).thenReturn(true);
    when(getUserPort.getUserById(userId)).thenReturn(Optional.of(user));
    when(generatorTokenPort.createRefreshToken(user)).thenReturn("refresh-token");
    when(generatorTokenPort.createAccessToken(user)).thenReturn("access-token");

    LoginResult result = loginService.login(new LoginCommand("happykoo@example.com", "plain-password"));

    assertEquals("access-token", result.accessToken());
    assertEquals("refresh-token", result.refreshToken());
    verify(saveTokenPort).saveRefreshToken(userId, "refresh-token");
  }

  @Test
  @DisplayName("login() :: 비밀번호가 일치하지 않으면 예외 발생")
  void loginTest2() {
    UUID userId = UUID.randomUUID();
    UserAccount account = new UserAccount(userId, "happykoo@example.com", "encoded-password");

    when(getUserAccountPort.getUserAccountByEmail("happykoo@example.com"))
        .thenReturn(Optional.of(account));
    when(encryptPasswordPort.matches("wrong-password", "encoded-password")).thenReturn(false);

    assertThrows(
        IllegalStateException.class,
        () -> loginService.login(new LoginCommand("happykoo@example.com", "wrong-password"))
    );

    verify(getUserPort, never()).getUserById(any());
  }

  @Test
  @DisplayName("login() :: 비활성 사용자면 예외 발생")
  void loginTest3() {
    UUID userId = UUID.randomUUID();
    UserAccount account = new UserAccount(userId, "happykoo@example.com", "encoded-password");
    User user = User.createMasterUser(userId, "happykoo", UserStatus.INACTIVE);

    when(getUserAccountPort.getUserAccountByEmail("happykoo@example.com"))
        .thenReturn(Optional.of(account));
    when(encryptPasswordPort.matches("plain-password", "encoded-password")).thenReturn(true);
    when(getUserPort.getUserById(userId)).thenReturn(Optional.of(user));

    assertThrows(
        IllegalStateException.class,
        () -> loginService.login(new LoginCommand("happykoo@example.com", "plain-password"))
    );
  }

  @Test
  @DisplayName("refreshAccessToken() :: 토큰 payload 에 userId 가 없으면 예외 발생")
  void refreshAccessTokenTest1() {
    when(getTokenPort.getRefreshTokenPayload("refresh-token"))
        .thenReturn(new RefreshTokenPayload(null));

    assertThrows(
        IllegalStateException.class,
        () -> loginService.refreshAccessToken("refresh-token")
    );
  }

  @Test
  @DisplayName("refreshAccessToken() :: 정상 요청 시 새 access token 반환")
  void refreshAccessTokenTest2() {
    UUID userId = UUID.randomUUID();
    User user = User.createMasterUser(userId, "happykoo", UserStatus.ACTIVE);

    when(getTokenPort.getRefreshTokenPayload("refresh-token"))
        .thenReturn(new RefreshTokenPayload(userId.toString()));
    when(getUserPort.getUserById(userId)).thenReturn(Optional.of(user));
    when(generatorTokenPort.createAccessToken(user)).thenReturn("new-access-token");

    RefreshAccessTokenResult result = loginService.refreshAccessToken("refresh-token");

    assertEquals("new-access-token", result.accessToken());
  }

  @Test
  @DisplayName("getLoginUserInfo() :: 계정 정보와 프로필 정보를 합쳐 반환")
  void getLoginUserInfoTest1() {
    UUID userId = UUID.randomUUID();
    Instant changedAt = Instant.now();

    when(getUserAccountPort.getUserAccountViewById(userId))
        .thenReturn(Optional.of(new UserAccountView(userId, "happykoo@example.com", changedAt)));
    when(getUserPort.getUserProfileById(userId))
        .thenReturn(Optional.of(new UserProfile("happykoo")));

    GetLoginUserInfo result = loginService.getLoginUserInfo(userId);

    assertEquals(userId, result.userId());
    assertEquals("happykoo", result.displayName());
    assertEquals("happykoo@example.com", result.email());
    assertEquals(changedAt, result.passwordChangedAt());
  }

  @Test
  @DisplayName("getLoginUserInfo() :: 계정 정보가 없으면 예외 발생")
  void getLoginUserInfoTest2() {
    UUID userId = UUID.randomUUID();
    when(getUserAccountPort.getUserAccountViewById(userId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> loginService.getLoginUserInfo(userId));
  }

  @Test
  @DisplayName("logout() :: refresh token 삭제를 위임")
  void logoutTest1() {
    loginService.logout("refresh-token");

    verify(saveTokenPort).removeRefreshToken("refresh-token");
  }
}
