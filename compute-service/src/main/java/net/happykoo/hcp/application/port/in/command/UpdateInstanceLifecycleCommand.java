package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record UpdateInstanceLifecycleCommand(
    UUID instanceId,
    UUID ownerId,
    String idempotencyKey
) {

  public String payload() {
    return instanceId.toString() + ownerId.toString();
  }
}
