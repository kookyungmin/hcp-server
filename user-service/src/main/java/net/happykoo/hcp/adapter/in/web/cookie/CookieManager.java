package net.happykoo.hcp.adapter.in.web.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CookieProperties.class)
public class CookieManager {

  private final CookieProperties cookieProperties;

  public ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(cookieProperties.getCookieName(), refreshToken)
        .httpOnly(true)
        .secure(cookieProperties.isSecure())
        .path("/")
        .maxAge(cookieProperties.getExpireTime())
        .sameSite("Strict")
        .build();
  }
}
