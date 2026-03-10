package net.happykoo.hcp.adapter.out.websocket.payload;

public record WorkerTerminalMessage(
    WorkerTerminalMessageType type,
    String sessionId,
    String instanceId
) {

  public static WorkerTerminalMessage open(
      String sessionId,
      String instanceId
  ) {
    return new WorkerTerminalMessage(WorkerTerminalMessageType.OPEN, sessionId, instanceId);
  }

  public static WorkerTerminalMessage close(
      String sessionId
  ) {
    return new WorkerTerminalMessage(WorkerTerminalMessageType.CLOSE, sessionId, null);
  }
}
