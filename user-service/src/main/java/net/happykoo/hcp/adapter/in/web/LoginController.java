package net.happykoo.hcp.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.auth.UserReadPermission;
import net.happykoo.hcp.adapter.in.web.cookie.CookieManager;
import net.happykoo.hcp.adapter.in.web.request.LoginRequest;
import net.happykoo.hcp.adapter.in.web.response.GetCurrentUserResponse;
import net.happykoo.hcp.adapter.in.web.response.LoginResponse;
import net.happykoo.hcp.adapter.in.web.response.RefreshAccessTokenResponse;
import net.happykoo.hcp.application.port.in.LoginUseCase;
import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.common.annotation.CurrentActor;
import net.happykoo.hcp.common.annotation.WebAdapter;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import net.happykoo.hcp.common.web.security.Actor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class LoginController {

  private final LoginUseCase loginUseCase;
  private final CookieManager cookieManager;

  @GetMapping("/me")
  @UserReadPermission
  public CommonResponseEntity<GetCurrentUserResponse> getCurrentUser(
      @CurrentActor Actor actor
  ) {
    //TODO: 후에 getUserUseCase 로 옮겨서 구현
    var loginUserResult = loginUseCase.getLoginUserInfo(UUID.fromString(actor.userId()));
    return CommonResponseEntity.ok(new GetCurrentUserResponse(
        loginUserResult.userId().toString(),
        loginUserResult.displayName(),
        loginUserResult.email(),
        loginUserResult.passwordChangedAt(),
        actor.scopes()
    ));
  }

  @PostMapping("/login")
  public CommonResponseEntity<LoginResponse> login(
      @RequestBody @Valid LoginRequest request,
      HttpServletResponse response
  ) {
    var loginResult = loginUseCase.login(new LoginCommand(request.email(), request.password()));

    response.setHeader(HttpHeaders.SET_COOKIE,
        cookieManager.createRefreshTokenCookie(loginResult.refreshToken()).toString());

    return CommonResponseEntity.ok(new LoginResponse(loginResult.accessToken()));
  }

  @PostMapping("/logout")
  public CommonResponseEntity<Void> logout(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    var refreshToken = cookieManager.getRefreshToken(request.getCookies());
    loginUseCase.logout(refreshToken);

    response.setHeader(HttpHeaders.SET_COOKIE,
        cookieManager.deleteRefreshToken().toString());

    return CommonResponseEntity.ok();
  }

  @PostMapping("/token/refresh")
  public CommonResponseEntity<RefreshAccessTokenResponse> refreshAccessToken(
      HttpServletRequest request
  ) {
    var refreshToken = cookieManager.getRefreshToken(request.getCookies());
    var refreshResult = loginUseCase.refreshAccessToken(refreshToken);

    return CommonResponseEntity.ok(new RefreshAccessTokenResponse(refreshResult.accessToken()));
  }
}
