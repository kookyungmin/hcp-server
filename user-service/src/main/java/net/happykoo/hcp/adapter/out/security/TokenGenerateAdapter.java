package net.happykoo.hcp.adapter.out.security;

import java.util.UUID;
import net.happykoo.hcp.application.port.out.GeneratorTokenPort;
import net.happykoo.hcp.common.utils.jwt.JwtProperties;
import net.happykoo.hcp.common.utils.jwt.JwtProvider;
import net.happykoo.hcp.common.web.annotation.SecurityOutAdapter;
import net.happykoo.hcp.common.web.security.Actor;
import net.happykoo.hcp.domain.PermissionCode;
import net.happykoo.hcp.domain.User;
import net.happykoo.hcp.infrastructure.properties.AccessTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SecurityOutAdapter
@EnableConfigurationProperties(AccessTokenProperties.class)
public class TokenGenerateAdapter implements GeneratorTokenPort {

  private final JwtProvider<Actor> jwtProvider;
  private final AccessTokenProperties accessTokenProperties;

  public TokenGenerateAdapter(
      AccessTokenProperties accessTokenProperties
  ) {
    this.accessTokenProperties = accessTokenProperties;
    this.jwtProvider = new JwtProvider<>(
        new JwtProperties(accessTokenProperties.getSecretKey()));
  }

  @Override
  public String createRefreshToken(User user) {
    return UUID.randomUUID().toString();
  }

  @Override
  public String createAccessToken(User user) {
    var actor = new Actor(
        user.getId().toString(),
        user.getPermissions()
            .stream()
            .map(PermissionCode::value)
            .toList());
    return jwtProvider.generateToken(actor, accessTokenProperties.getExpireTime());
  }
}
