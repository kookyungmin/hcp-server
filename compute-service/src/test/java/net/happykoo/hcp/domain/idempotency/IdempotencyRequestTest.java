package net.happykoo.hcp.domain.idempotency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdempotencyRequestTest {

  @Test
  @DisplayName("IdempotencyRequest :: 생성자 값이 그대로 보존")
  void constructorTest1() {
    UUID ownerId = UUID.randomUUID();
    IdempotencyRequest request = new IdempotencyRequest(
        ownerId,
        "idem-1",
        IdempotencyCommandType.INSTANCE_PROVISIONING,
        "hash",
        null
    );

    assertEquals(ownerId, request.getOwnerId());
    assertEquals("idem-1", request.getIdempotencyKey());
    assertEquals(IdempotencyCommandType.INSTANCE_PROVISIONING, request.getCommandType());
    assertEquals("hash", request.getRequestHash());
  }
}
