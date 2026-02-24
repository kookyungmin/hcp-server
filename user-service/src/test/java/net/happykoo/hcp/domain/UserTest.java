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
  @DisplayName("User 생성 시 기본적으로 USER 권한과 ACTIVE 상태를 가짐")
  void setUp() {
    user = new User(UUID.randomUUID().toString(), "happykoo");

    assertEquals(1, user.getRoles().size());
    assertEquals(UserRole.USER, user.getRoles().iterator().next());
    assertEquals(UserStatus.ACTIVE, user.getStatus());
  }

  @Test
  @DisplayName("addRole() :: 정상적인 role 추가")
  void addRoleTest1() {
    user.addRole(UserRole.ADMIN);
    user.addRole(UserRole.SYSTEM);

    assertEquals(3, user.getRoles().size());
  }

  @Test
  @DisplayName("addRole() :: 이미 존재하는 role 을 추가하는 경우 중복 제거")
  void addRoleTest2() {
    user.addRole(UserRole.ADMIN);
    user.addRole(UserRole.ADMIN);

    assertEquals(2, user.getRoles().size());
  }

  @Test
  @DisplayName("removeRole() :: 존재하지 않는 role을 제거하는 경우 예외 발생")
  void removeRoleTest1() {
    assertThrows(IllegalStateException.class, () -> user.removeRole(UserRole.ADMIN));
  }

  @Test
  @DisplayName("removeRole() :: role이 1개 밖에 없는 경우 예외 발생")
  void removeRoleTest2() {
    assertThrows(IllegalStateException.class, () -> user.removeRole(UserRole.USER));
  }

  @Test
  @DisplayName("removeRole() :: 정상적으로 role을 제거한 경우")
  void removeRoleTest3() {
    user.addRole(UserRole.ADMIN);
    user.removeRole(UserRole.ADMIN);

    assertEquals(1, user.getRoles().size());
  }
}
