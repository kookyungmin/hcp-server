package net.happykoo.hcp.adapter.out.persistence;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.GetTokenPort;
import net.happykoo.hcp.application.port.out.SaveTokenPort;
import net.happykoo.hcp.application.port.out.data.RefreshTokenPayload;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.infrastructure.properties.RefreshTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

@PersistenceAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(RefreshTokenProperties.class)
public class TokenPersistenceAdapter implements SaveTokenPort, GetTokenPort {

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

  @Override
  public void removeRefreshToken(String refreshToken) {
    redisTemplate.delete(generateRefreshTokenKey(refreshToken));
  }

  @Override
  public RefreshTokenPayload getRefreshTokenPayload(String refreshToken) {
    return new RefreshTokenPayload(redisTemplate.opsForValue()
        .get(generateRefreshTokenKey(refreshToken)));
  }

  private String generateRefreshTokenKey(String refreshToken) {
    return refreshTokenProperties.getPrefix()
        + ":"
        + refreshToken;
  }
}
