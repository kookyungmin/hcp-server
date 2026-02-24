package net.happykoo.hcp.common.web;

import java.time.Instant;
import lombok.Getter;

@Getter
public class SuccessResponse<T> extends ApiResponse {

  private final T body;

  public SuccessResponse(
      Instant timestamp,
      int status,
      T body,
      String path) {
    super(timestamp, status, path);
    this.body = body;
  }
}
