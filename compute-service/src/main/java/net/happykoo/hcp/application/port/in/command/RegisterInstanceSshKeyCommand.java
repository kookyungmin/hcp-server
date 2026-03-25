package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record RegisterInstanceSshKeyCommand(
    UUID instanceId,
    UUID requesterId,
    String idempotencyKey,
    String keyName,
    String publicKey,
    String requestId
) {

  public String payload() {
    return keyName + publicKey;
  }
}
