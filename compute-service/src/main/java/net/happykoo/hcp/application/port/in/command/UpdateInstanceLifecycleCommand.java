package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record UpdateInstanceLifecycleCommand(
    UUID instanceId,
    UUID requesterId,
    String idempotencyKey
) {

  public String payload() {
    return instanceId.toString() + requesterId.toString();
  }
}
