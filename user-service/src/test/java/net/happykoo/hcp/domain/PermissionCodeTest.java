package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PermissionCodeTest {

  @Test
  @DisplayName("equals/hashCode :: value가 같으면 동등하고 hashCode도 동일")
  void equalsAndHashCodeTest1() {
    PermissionCode left = new PermissionCode("USER_READ");
    PermissionCode right = new PermissionCode("USER_READ");

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  @DisplayName("equals/hashCode :: value가 다르면 동등하지 않고 hashCode도 다름")
  void equalsAndHashCodeTest2() {
    PermissionCode left = new PermissionCode("USER_READ");
    PermissionCode right = new PermissionCode("USER_WRITE");

    assertNotEquals(left, right);
    assertNotEquals(left.hashCode(), right.hashCode());
  }
}
