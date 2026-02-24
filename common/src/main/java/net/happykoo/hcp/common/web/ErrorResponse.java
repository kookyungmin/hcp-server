package net.happykoo.hcp.common.web;

import java.time.Instant;
import lombok.Getter;

@Getter
public class ErrorResponse extends ApiResponse {

  private String code;
  private String message;

  public ErrorResponse(
      int status,
      String code,
      String message,
      String path
  ) {
    super(Instant.now(), status, path);
    this.code = code;
    this.message = message;
  }
}
