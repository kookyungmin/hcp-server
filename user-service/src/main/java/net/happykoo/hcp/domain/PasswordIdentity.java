package net.happykoo.hcp.domain;

import java.time.Instant;
import lombok.Getter;

@Getter
public class PasswordIdentity extends UserIdentity {

  private Email email;
  private String passwordHash;
  private Instant passwordChangedAt;

  public PasswordIdentity(
      String userId,
      String email,
      String passwordHash
  ) {
    super(userId, UserIdentityType.PASSWORD, UserIdentityStatus.ACTIVE);
    this.email = new Email(email);
    this.passwordHash = passwordHash;
  }

  public void changePassword(String newPasswordHash) {
    if (passwordHash.equals(newPasswordHash)) {
      throw new IllegalArgumentException("The Password is same as the old one.");
    }
    this.passwordHash = newPasswordHash;
    this.passwordChangedAt = Instant.now();
  }
}
