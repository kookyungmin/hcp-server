package net.happykoo.hcp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  @DisplayName("Email 생성자 :: 정상적인 이메일 형식인 경우 객체 생성")
  void constructorTest1() {
    Email email = new Email("happykoo@example.com");

    assertEquals("happykoo@example.com", email.getValue());
  }

  @Test
  @DisplayName("Email 생성자 :: @가 없는 경우 예외 발생")
  void constructorTest2() {
    assertThrows(IllegalArgumentException.class, () -> new Email("happykoo.example.com"));
  }

  @Test
  @DisplayName("Email 생성자 :: 도메인 확장자가 없는 경우 예외 발생")
  void constructorTest3() {
    assertThrows(IllegalArgumentException.class, () -> new Email("happykoo@example"));
  }

  @Test
  @DisplayName("Email 생성자 :: 로컬 파트에 + 포함된 정상 이메일 허용")
  void constructorTest4() {
    Email email = new Email("happykoo+test@example.com");

    assertEquals("happykoo+test@example.com", email.getValue());
  }
}
