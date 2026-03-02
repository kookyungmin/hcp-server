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
    this(userId, email, passwordHash, Instant.now());
  }

  public UserAccount(
      UUID userId,
      String email,
      String passwordHash,
      Instant passwordChangedAt
  ) {
    this.userId = userId;
    this.email = new Email(email);
    this.passwordHash = passwordHash;
    this.passwordChangedAt = passwordChangedAt;
  }

  public void changePassword(String newPasswordHash) {
    if (passwordHash.equals(newPasswordHash)) {
      throw new IllegalArgumentException("새 비밀번호가 기존 비밀번호와 같습니다.");
    }
    this.passwordHash = newPasswordHash;
    this.passwordChangedAt = Instant.now();
  }
}
