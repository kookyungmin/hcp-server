package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.application.port.out.data.RefreshTokenPayload;

public interface GetTokenPort {

  RefreshTokenPayload getRefreshTokenPayload(String refreshToken);
}
