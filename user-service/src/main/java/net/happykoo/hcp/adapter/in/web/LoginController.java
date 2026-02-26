package net.happykoo.hcp.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.cookie.CookieManager;
import net.happykoo.hcp.adapter.in.web.request.LoginRequest;
import net.happykoo.hcp.adapter.in.web.response.LoginResponse;
import net.happykoo.hcp.adapter.in.web.response.RefreshAccessTokenResponse;
import net.happykoo.hcp.application.port.in.LoginUseCase;
import net.happykoo.hcp.application.port.in.command.LoginCommand;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class LoginController {

  private final LoginUseCase loginUseCase;
  private final CookieManager cookieManager;

  @GetMapping("/me")
  @PreAuthorize("hasAuthority('user:read')")
  public CommonResponseEntity<Void> getCurrentUser() {
    return null;
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
