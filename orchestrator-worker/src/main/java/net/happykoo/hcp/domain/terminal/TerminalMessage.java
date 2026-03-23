package net.happykoo.hcp.domain.terminal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TerminalMessage(
    TerminalMessageType type,
    String sessionId,
    String instanceId,
    String message,
    Integer cols,
    Integer rows
) {

  public static TerminalMessage open(
      String sessionId
  ) {
    return new TerminalMessage(
        TerminalMessageType.OPEN,
        sessionId,
        null,
        null,
        null,
        null
    );
  }

  public static TerminalMessage close(
      String sessionId
  ) {
    return new TerminalMessage(
        TerminalMessageType.CLOSE,
        sessionId,
        null,
        null,
        null,
        null
    );
  }

  public static TerminalMessage error(
      String errorMessage
  ) {
    return new TerminalMessage(
        TerminalMessageType.ERROR,
        null,
        null,
        errorMessage,
        null,
        null
    );
  }
}
