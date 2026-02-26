package net.happykoo.hcp.common.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponseEntity<T>(
    Instant timestamp,
    int status,
    String code,
    T body,
    String message,
    String path
) {

  public CommonResponseEntity(
      String message,
      String path,
      HttpStatus status
  ) {
    this(Instant.now(), status.value(), status.getReasonPhrase(), null, message, path);
  }

  public CommonResponseEntity(
      T body,
      HttpStatus status
  ) {
    this(Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        body,
        null,
        null);
  }

  public CommonResponseEntity(
      HttpStatus status
  ) {
    this(Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        null,
        null,
        null);
  }

  public static CommonResponseEntity<String> error(
      String message,
      String path,
      HttpStatus status
  ) {
    return new CommonResponseEntity<>(
        message,
        path,
        status);
  }

  public static <T> CommonResponseEntity<T> ok(T body) {
    return new CommonResponseEntity<>(body, HttpStatus.OK);
  }

  public static CommonResponseEntity<Void> ok() {
    return new CommonResponseEntity<>(HttpStatus.OK);
  }
}
