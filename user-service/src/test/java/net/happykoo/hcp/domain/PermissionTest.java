package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PermissionTest {

  @Test
  @DisplayName("Permission 생성자 :: 문자열 code 로 PermissionCode 를 생성")
  void constructorTest1() {
    Permission permission = new Permission("USER_READ", "사용자 조회");

    assertEquals("USER_READ", permission.code().value());
    assertEquals("사용자 조회", permission.description());
  }
}
