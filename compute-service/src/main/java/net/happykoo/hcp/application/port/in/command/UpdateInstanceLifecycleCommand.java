package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record UpdateInstanceLifecycleCommand(
    UUID instanceId,
    UUID requesterId,
    String idempotencyKey,
    String requestId
) {

  public String payload() {
    return instanceId.toString() + requesterId.toString();
  }
}
