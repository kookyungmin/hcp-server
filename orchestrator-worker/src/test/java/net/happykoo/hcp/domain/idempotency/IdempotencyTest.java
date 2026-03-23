package net.happykoo.hcp.domain.idempotency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdempotencyTest {

  @Test
  @DisplayName("Idempotency :: success/failed 호출 시 상태 변경")
  void statusChangeTest1() {
    Idempotency idempotency = new Idempotency(
        UUID.randomUUID(),
        IdempotencyStatus.PROCESSING,
        Instant.now().plusSeconds(30)
    );

    idempotency.success();
    assertEquals(IdempotencyStatus.SUCCESS, idempotency.getStatus());

    idempotency.failed();
    assertEquals(IdempotencyStatus.FAILED, idempotency.getStatus());
  }
}
