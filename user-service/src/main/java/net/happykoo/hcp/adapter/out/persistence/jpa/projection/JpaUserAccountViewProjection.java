package net.happykoo.hcp.adapter.out.persistence.jpa.projection;

import java.time.Instant;
import java.util.UUID;

public interface JpaUserAccountViewProjection {

  UUID getUserId();

  String getEmail();

  Instant getLastChangedPasswordAt();
}
