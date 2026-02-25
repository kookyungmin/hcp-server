package net.happykoo.hcp.adapter.in.web.cookie;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.infrastructure.properties.RefreshTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RefreshTokenProperties.class)
public class CookieManager {

  private final RefreshTokenProperties refreshTokenProperties;

  public ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(refreshTokenProperties.getPrefix(), refreshToken)
        .httpOnly(true)
        .secure(refreshTokenProperties.isSecure())
        .path("/")
        .maxAge(refreshTokenProperties.getExpireTime())
        .sameSite("Strict")
        .build();
  }
}
