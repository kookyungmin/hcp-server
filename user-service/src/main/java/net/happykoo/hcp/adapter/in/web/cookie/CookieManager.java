package net.happykoo.hcp.adapter.in.web.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CookieProperties.class)
public class CookieManager {

  private final CookieProperties cookieProperties;

  public void setRefreshTokenCookie(String refreshToken) {

  }
}
