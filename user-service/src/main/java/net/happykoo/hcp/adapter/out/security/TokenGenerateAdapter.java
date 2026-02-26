package net.happykoo.hcp.adapter.out.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.GeneratorTokenPort;
import net.happykoo.hcp.common.annotation.SecurityOutAdapter;
import net.happykoo.hcp.common.web.security.jwt.JwtClaims;
import net.happykoo.hcp.common.web.security.jwt.JwtProvider;
import net.happykoo.hcp.domain.PermissionCode;
import net.happykoo.hcp.domain.User;

@SecurityOutAdapter
@RequiredArgsConstructor
public class TokenGenerateAdapter implements GeneratorTokenPort {

  private final JwtProvider jwtProvider;

  @Override
  public String createRefreshToken(User user) {
    return UUID.randomUUID().toString();
  }

  @Override
  public String createAccessToken(User user) {
    return jwtProvider.createAccessToken(new JwtClaims(
        user.getId().toString(),
        user.getPermissions()
            .stream()
            .map(PermissionCode::value)
            .toList()
    ));
  }
}
