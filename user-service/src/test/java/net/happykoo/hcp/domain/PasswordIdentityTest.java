package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordIdentityTest {

  @Test
  @DisplayName("changePassword() :: 기존 비밀번호와 동일한 경우 예외 발생")
  void changePasswordTest1() {
    PasswordIdentity passwordIdentity =
        new PasswordIdentity("user-1", "happykoo@example.com", "old-password-hash");

    assertThrows(
        IllegalArgumentException.class,
        () -> passwordIdentity.changePassword("old-password-hash")
    );
  }

  @Test
  @DisplayName("changePassword() :: 정상적으로 비밀번호를 변경한 경우 hash와 변경 시각 갱신")
  void changePasswordTest2() {
    PasswordIdentity passwordIdentity =
        new PasswordIdentity("user-1", "happykoo@example.com", "old-password-hash");

    passwordIdentity.changePassword("new-password-hash");

    assertEquals("new-password-hash", passwordIdentity.getPasswordHash());
    assertNotNull(passwordIdentity.getPasswordChangedAt());
  }
}
