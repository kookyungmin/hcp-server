package net.happykoo.hcp.adapter.out.persistence;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.SaveTokenPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.infrastructure.properties.RefreshTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

@PersistenceAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(RefreshTokenProperties.class)
public class TokenPersistenceAdapter implements SaveTokenPort {

  private final StringRedisTemplate redisTemplate;
  private final RefreshTokenProperties refreshTokenProperties;

  @Override
  public void saveRefreshToken(UUID userId, String refreshToken) {
    redisTemplate.opsForValue()
        .set(
            generateRefreshTokenKey(refreshToken),
            userId.toString(),
            Duration.ofSeconds(refreshTokenProperties.getExpireTime())
        );
  }

  public String generateRefreshTokenKey(String refreshToken) {
    return refreshTokenProperties.getPrefix()
        + ":"
        + refreshToken;
  }
}
