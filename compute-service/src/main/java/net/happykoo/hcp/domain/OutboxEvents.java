package net.happykoo.hcp.domain;

import java.util.UUID;

public class OutboxEvents {

  private UUID eventId;
  private String eventType;
  private String payload;
  private OutboxStatus status;
  private int retryCount;

}
