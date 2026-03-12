package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record UpdateInstanceSpecCommand(
    UUID requesterId,
    UUID instanceId,
    String idempotencyKey,
    String specCode,
    String storageType,
    int storageSize
) {

  public String payload() {
    return specCode + storageType + storageSize;
  }
}
