package net.happykoo.hcp.adapter.in.websocket.handler;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.websocket.session.TerminalWebSocketSessionRegistry;
import net.happykoo.hcp.application.port.in.CloseTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalBinaryInputUseCase;
import net.happykoo.hcp.application.port.in.OpenTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.ResizeTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerminalWebSocketHandler extends AbstractWebSocketHandler {

  private final OpenTerminalSessionUseCase openTerminalSessionUseCase;
  private final HandleTerminalBinaryInputUseCase handleTerminalBinaryInputUseCase;
  private final CloseTerminalSessionUseCase closeTerminalSessionUseCase;
  private final ResizeTerminalSessionUseCase resizeTerminalSessionUseCase;
  private final TerminalWebSocketSessionRegistry sessionRegistry;

  @Override
  protected void handleBinaryMessage(
      @NonNull WebSocketSession session,
      @NonNull BinaryMessage message
  ) {
    var sessionId = sessionRegistry.getSessionId(session);
    if (sessionId != null) {
      try {
        handleTerminalBinaryInputUseCase.handle(new HandleTerminalBinaryInputCommand(
            sessionId,
            message.getPayload().array()
        ));
      } catch (IllegalArgumentException | IllegalStateException e) {
        closeSession(session, CloseStatus.BAD_DATA, e.getMessage());
      } catch (Exception e) {
        closeSession(session, CloseStatus.SERVER_ERROR, e.getMessage());
      }
    }
  }

  @Override
  protected void handleTextMessage(
      @NonNull WebSocketSession session,
      @NonNull TextMessage message
  ) {
    try {
      var terminalMessage = new Gson().fromJson(message.getPayload(), TerminalMessage.class);
      switch (terminalMessage.type()) {
        case OPEN -> openTerminalSession(terminalMessage, session);
        case RESIZE -> resizeTerminalSessionUseCase.resize(terminalMessage);
        case CLOSE -> closeTerminalSessionUseCase.close(
            new CloseTerminalSessionCommand(terminalMessage.sessionId()));
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      closeSession(session, CloseStatus.BAD_DATA, e.getMessage());
    } catch (Exception e) {
      closeSession(session, CloseStatus.SERVER_ERROR, e.getMessage());
    }
  }

  private void openTerminalSession(
      TerminalMessage terminalMessage,
      WebSocketSession session
  ) {
    sessionRegistry.register(terminalMessage.sessionId(), session);
    openTerminalSessionUseCase.open(new OpenTerminalSessionCommand(
        terminalMessage.sessionId(),
        UUID.fromString(terminalMessage.instanceId())
    ));
  }

  @Override
  public void afterConnectionClosed(
      @NonNull WebSocketSession session,
      @NonNull CloseStatus status
  ) {
    var sessionId = sessionRegistry.getSessionId(session);
    if (sessionId != null) {
      try {
        closeTerminalSessionUseCase.close(new CloseTerminalSessionCommand(sessionId));
      } finally {
        sessionRegistry.remove(sessionId);
      }
    }
  }

  @Override
  public void handleTransportError(
      @NonNull WebSocketSession session,
      @NonNull Throwable exception
  ) {
    closeSession(session, CloseStatus.SERVER_ERROR, exception.getMessage());
  }

  private void closeSession(
      WebSocketSession session,
      CloseStatus closeStatus,
      String reason
  ) {
    try {
      if (session.isOpen()) {
        session.sendMessage(new TextMessage(new Gson().toJson(TerminalMessage.error(reason))));
        session.close(closeStatus);
      }
    } catch (IOException e) {
      log.error("Failed to close socket session", e);
    }
  }
}
