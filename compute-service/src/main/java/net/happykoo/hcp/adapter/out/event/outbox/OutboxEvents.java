package net.happykoo.hcp.adapter.out.event.outbox;

import java.util.UUID;

public class OutboxEvents {

  private UUID eventId;
  private String eventType; //topic
  private String payload;
  private OutboxStatus status;
  private int retryCount;

}
