package net.happykoo.hcp.adapter.out.websocket.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WorkerTerminalMessage(
    WorkerTerminalMessageType type,
    String sessionId,
    String instanceId,
    String message,
    Integer cols,
    Integer rows
) {

  public static WorkerTerminalMessage open(
      String sessionId,
      String instanceId
  ) {
    return new WorkerTerminalMessage(
        WorkerTerminalMessageType.OPEN,
        sessionId,
        instanceId,
        null,
        null,
        null
    );
  }

  public static WorkerTerminalMessage close(
      String sessionId
  ) {
    return new WorkerTerminalMessage(
        WorkerTerminalMessageType.CLOSE,
        sessionId,
        null,
        null,
        null,
        null
    );
  }

  public static WorkerTerminalMessage resize(
      String sessionId,
      Integer cols,
      Integer rows
  ) {
    return new WorkerTerminalMessage(
        WorkerTerminalMessageType.RESIZE,
        sessionId,
        null,
        null,
        cols,
        rows
    );
  }
}
