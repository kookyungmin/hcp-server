package net.happykoo.hcp.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserAccount {

  private UUID userId;
  private Email email;
  private String passwordHash;
  private Instant passwordChangedAt;

  public UserAccount(
      UUID userId,
      String email,
      String passwordHash
  ) {
    this.userId = userId;
    this.email = new Email(email);
    this.passwordHash = passwordHash;
    this.passwordChangedAt = Instant.now();
  }

  public void changePassword(String newPasswordHash) {
    if (passwordHash.equals(newPasswordHash)) {
      throw new IllegalArgumentException("The Password is same as the old one.");
    }
    this.passwordHash = newPasswordHash;
    this.passwordChangedAt = Instant.now();
  }
}
