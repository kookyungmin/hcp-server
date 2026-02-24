package net.happykoo.hcp.domain;

import java.time.Instant;
import lombok.Getter;

@Getter
public abstract class UserIdentity {

  private final String userId;
  private final UserIdentityType type;
  private UserIdentityStatus status;


  private final Instant createdAt;
  private Instant lastUsedAt;

  public UserIdentity(
      String userId,
      UserIdentityType type,
      UserIdentityStatus status
  ) {
    this.userId = userId;
    this.type = type;
    this.status = status;
    this.createdAt = Instant.now();
  }

  public void updateStatus(UserIdentityStatus status) {
    this.status = status;
  }

  public void refreshLastUsedAt() {
    this.lastUsedAt = Instant.now();
  }
}
