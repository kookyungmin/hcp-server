package net.happykoo.hcp.application.port.in.command;

import java.util.Set;
import java.util.UUID;

public record UpdateInstanceTagCommand(
    UUID requesterId,
    UUID instanceId,
    String tags
) {

  public Set<String> tagSet() {
    if (tags == null) {
      return Set.of();
    }
    return Set.of(tags.split(","));
  }
}
