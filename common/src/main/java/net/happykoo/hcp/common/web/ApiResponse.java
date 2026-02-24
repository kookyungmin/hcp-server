package net.happykoo.hcp.common.web;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse {

  private final Instant timestamp;
  private final int status;
  private final String path;
}
