package net.happykoo.hcp.adapter.out.websocket;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.websocket.payload.WorkerTerminalMessage;
import net.happykoo.hcp.application.port.out.SendTerminalCommandResultPort;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import net.happykoo.hcp.domain.terminal.TerminalSession;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@RequiredArgsConstructor
public class OrchestratorWorkerWebSocketHandler extends AbstractWebSocketHandler {

  private final TerminalSession terminalSession;
  private final SendTerminalCommandResultPort sendTerminalCommandResultPort;
  private final OrchestratorWorkerWebSocketAdapter orchestratorWorkerWebSocketAdapter;

  @Override
  protected void handleBinaryMessage(
      @NonNull WebSocketSession session,
      BinaryMessage message
  ) {
    sendTerminalCommandResultPort.send(
        terminalSession.getSessionId(),
        message.getPayload().array());
  }

  @Override
  protected void handleTextMessage(
      @NonNull WebSocketSession session,
      @NonNull TextMessage message
  ) {
    var workerTerminalMessage = new Gson().fromJson(message.getPayload(),
        WorkerTerminalMessage.class);

    switch (workerTerminalMessage.type()) {
      case OPEN -> sendTerminalCommandResultPort.send(
          terminalSession.getSessionId(),
          TerminalMessage.ready()
      );
      case CLOSE -> sendTerminalCommandResultPort.send(
          terminalSession.getSessionId(),
          TerminalMessage.close()
      );
      case ERROR -> sendTerminalCommandResultPort.send(
          terminalSession.getSessionId(),
          TerminalMessage.error(workerTerminalMessage.message())
      );
    }
  }

  @Override
  public void afterConnectionClosed(
      @NonNull WebSocketSession session,
      @NonNull CloseStatus status
  ) {
    var sessionId = terminalSession.getSessionId();
    orchestratorWorkerWebSocketAdapter.removeSession(sessionId);
    sendTerminalCommandResultPort.close(sessionId);
  }

  @Override
  public void handleTransportError(
      WebSocketSession session,
      @NonNull Throwable exception) throws Exception {
    var sessionId = terminalSession.getSessionId();
    orchestratorWorkerWebSocketAdapter.removeSession(sessionId);
    sendTerminalCommandResultPort.send(sessionId, TerminalMessage.error(
        exception.getMessage()
    ));
    if (session.isOpen()) {
      session.close(CloseStatus.SERVER_ERROR);
    }
  }
}
