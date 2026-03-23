package net.happykoo.hcp.domain.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OutboxEventTest {

  @Test
  @DisplayName("OutboxEvent :: success/failed 호출 시 상태 변경")
  void statusChangeTest1() {
    OutboxEvent event = new OutboxEvent(
        UUID.randomUUID(),
        OutboxEventType.INSTANCE_PROVISIONING_EVENT,
        "{}",
        OutboxStatus.PENDING,
        0
    );

    event.success();
    assertEquals(OutboxStatus.SUCCESS, event.getStatus());

    event.failed();
    assertEquals(OutboxStatus.FAILED, event.getStatus());
  }
}
