package net.happykoo.hcp.adapter.in.web.cookie;

import jakarta.servlet.http.Cookie;
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
    return createCookie(
        refreshTokenProperties.getPrefix(),
        refreshToken,
        refreshTokenProperties.isSecure(),
        refreshTokenProperties.getExpireTime()
    );
  }

  public ResponseCookie deleteRefreshToken() {
    return createCookie(
        refreshTokenProperties.getPrefix(),
        "",
        refreshTokenProperties.isSecure(),
        0
    );
  }

  public String getRefreshToken(Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(refreshTokenProperties.getPrefix())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public ResponseCookie createCookie(
      String cookieName,
      String value,
      boolean isSecure,
      long maxAge
  ) {
    return ResponseCookie.from(cookieName, value)
        .httpOnly(true)
        .secure(isSecure)
        .path("/")
        .maxAge(maxAge)
        .sameSite("Lax")
        .build();
  }
}
