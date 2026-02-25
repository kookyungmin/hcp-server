package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserAccountTest {

  @Test
  @DisplayName("changePassword() :: 기존 비밀번호와 동일한 경우 예외 발생")
  void changePasswordTest1() {
    UserAccount userAccount =
        new UserAccount(UUID.randomUUID(), "happykoo@example.com", "old-password-hash");

    assertThrows(
        IllegalArgumentException.class,
        () -> userAccount.changePassword("old-password-hash")
    );
  }

  @Test
  @DisplayName("changePassword() :: 정상적으로 비밀번호를 변경한 경우 hash와 변경 시각 갱신")
  void changePasswordTest2() {
    UserAccount userAccount =
        new UserAccount(UUID.randomUUID(), "happykoo@example.com", "old-password-hash");

    userAccount.changePassword("new-password-hash");

    assertEquals("new-password-hash", userAccount.getPasswordHash());
    assertNotNull(userAccount.getPasswordChangedAt());
  }
}
