package net.happykoo.hcp.adapter.out.persistence;

import net.happykoo.hcp.application.port.out.GeneratorTokenPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.User;

@PersistenceAdapter
public class TokenPersistenceAdapter implements GeneratorTokenPort {

  @Override
  public String createRefreshToken(User user) {
    return "";
  }

  @Override
  public String createAccessToken(User user) {
    return "";
  }
}
