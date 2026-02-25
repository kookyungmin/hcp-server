package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.User;

public interface GeneratorTokenPort {

  String createRefreshToken(User user);

  String createAccessToken(User user);
}
