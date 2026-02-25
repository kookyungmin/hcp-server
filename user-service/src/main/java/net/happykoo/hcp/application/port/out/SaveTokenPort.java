package net.happykoo.hcp.application.port.out;

import java.util.UUID;

public interface SaveTokenPort {

  void saveRefreshToken(UUID userId, String refreshToken);
}
