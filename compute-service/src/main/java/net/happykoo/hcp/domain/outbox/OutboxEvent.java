package net.happykoo.hcp.domain.outbox;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OutboxEvent {

  private UUID eventId;
  private OutboxEventType eventType;
  private String payload;
  private OutboxStatus status;
  private int retryCount;

  public void success() {
    this.status = OutboxStatus.SUCCESS;
  }

  public void failed() {
    this.status = OutboxStatus.FAILED;
    this.retryCount++;
  }
}
