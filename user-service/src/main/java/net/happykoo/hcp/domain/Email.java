package net.happykoo.hcp.domain;

import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class Email {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  private final String value;

  public Email(String value) {
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("유효하지 않은 이메일 주소입니다.");
    }
    this.value = value;
  }
}
