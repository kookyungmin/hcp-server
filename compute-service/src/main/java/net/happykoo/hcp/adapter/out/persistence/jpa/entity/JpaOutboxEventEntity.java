package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.domain.outbox.OutboxEventType;
import net.happykoo.hcp.domain.outbox.OutboxStatus;

@Entity
@Table(name = "h_outbox_event")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaOutboxEventEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "event_id")
  private UUID eventId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private OutboxEventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private OutboxStatus status;

  @Column(name = "payload")
  private String payload;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "retry_count")
  private int retryCount;

  @Column(name = "claim_token")
  private UUID claimToken;

  public static JpaOutboxEventEntity from(OutboxEvent outboxEvent) {
    return new JpaOutboxEventEntity(
        outboxEvent.getEventId(),
        outboxEvent.getEventType(),
        outboxEvent.getStatus(),
        outboxEvent.getPayload(),
        outboxEvent.getRequestId(),
        outboxEvent.getRetryCount(),
        null
    );
  }

  public OutboxEvent toDomain() {
    return new OutboxEvent(
        eventId,
        eventType,
        payload,
        requestId,
        status,
        retryCount
    );
  }
}
