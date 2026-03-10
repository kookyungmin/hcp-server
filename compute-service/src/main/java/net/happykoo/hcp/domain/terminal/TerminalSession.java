package net.happykoo.hcp.domain.terminal;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TerminalSession {

  private String sessionId;
  private UUID instanceId;
  private TerminalUserContext userContext;

  public UUID getUserId() {
    return Optional.ofNullable(userContext)
        .map(TerminalUserContext::userId)
        .orElse(null);
  }
}
