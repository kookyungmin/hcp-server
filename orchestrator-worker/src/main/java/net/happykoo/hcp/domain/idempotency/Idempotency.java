package net.happykoo.hcp.domain.idempotency;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Idempotency {

  private UUID idempotencyKey;
  private IdempotencyStatus status;
  private Instant expiredAt;

  public void success() {
    this.status = IdempotencyStatus.SUCCESS;
  }

  public void failed() {
    this.status = IdempotencyStatus.FAILED;
  }

}
