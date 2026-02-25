package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  private User user;

  @BeforeEach
  @DisplayName("User 생성 시 기본적으로 빈 권한과 전달한 상태를 가짐")
  void setUp() {
    user = User.createMasterUser(UUID.randomUUID(), "happykoo", UserStatus.ACTIVE);

    assertEquals(0, user.getPermissions().size());
    assertEquals(UserStatus.ACTIVE, user.getStatus());
  }

  @Test
  @DisplayName("addRole() :: 정상적인 permission code 추가")
  void addPermissionTest1() {
    user.addPermission("USER_READ");
    user.addPermission("USER_WRITE");

    assertEquals(2, user.getPermissions().size());
  }

  @Test
  @DisplayName("addRole() :: 이미 존재하는 permission code를 추가하는 경우 중복 제거")
  void addPermissionTest2() {
    user.addPermission("USER_READ");
    user.addPermission("USER_READ");

    assertEquals(1, user.getPermissions().size());
  }

  @Test
  @DisplayName("removeRole() :: 존재하지 않는 permission code를 제거하는 경우 예외 발생")
  void removePermissionTest1() {
    assertThrows(IllegalStateException.class, () -> user.removePermission("USER_READ"));
  }

  @Test
  @DisplayName("removeRole() :: 추가된 permission code를 정상적으로 제거")
  void removePermissionTest2() {
    user.addPermission("USER_READ");
    user.removePermission("USER_READ");

    assertEquals(0, user.getPermissions().size());
  }
}
