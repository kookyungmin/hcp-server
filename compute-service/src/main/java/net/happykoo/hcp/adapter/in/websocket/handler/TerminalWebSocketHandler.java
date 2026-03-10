package net.happykoo.hcp.adapter.in.websocket.handler;

import com.google.gson.Gson;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.websocket.session.TerminalWebSocketSessionAttributes;
import net.happykoo.hcp.adapter.in.websocket.session.TerminalWebSocketSessionRegistry;
import net.happykoo.hcp.application.port.in.CloseTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalBinaryInputUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalControlUseCase;
import net.happykoo.hcp.application.port.in.OpenTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalControlCommand;
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
  private final CloseTerminalSessionUseCase closeTerminalSessionUseCase;
  private final HandleTerminalControlUseCase handleTerminalControlUseCase;
  private final HandleTerminalBinaryInputUseCase handleTerminalBinaryInputUseCase;
  private final TerminalWebSocketSessionRegistry sessionRegistry;

  @Override
  public void afterConnectionEstablished(
      @NonNull WebSocketSession session
  ) {
    try {
      sessionRegistry.register(session);
      TerminalWebSocketSessionAttributes attrs = TerminalWebSocketSessionAttributes.from(session);
      openTerminalSessionUseCase.open(new OpenTerminalSessionCommand(
          session.getId(),
          attrs.instanceId(),
          attrs.userId(),
          attrs.roles()
      ));
    } catch (Exception e) {
      closeSession(session, CloseStatus.SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void handleBinaryMessage(
      @NonNull WebSocketSession session,
      @NonNull BinaryMessage message
  ) {
    //binary stream
    try {
      handleTerminalBinaryInputUseCase.handle(new HandleTerminalBinaryInputCommand(
          session.getId(),
          message.getPayload().array()
      ));
    } catch (IllegalArgumentException | IllegalStateException e) {
      closeSession(session, CloseStatus.BAD_DATA, e.getMessage());
    } catch (Exception e) {
      closeSession(session, CloseStatus.SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void handleTextMessage(
      @NonNull WebSocketSession session,
      @NonNull TextMessage message
  ) {
    //resize, ping, close 등의 컨트롤 메시지 처리 지점
    try {
      handleTerminalControlUseCase.handle(new HandleTerminalControlCommand(
          session.getId(),
          message.getPayload()
      ));
    } catch (IllegalArgumentException | IllegalStateException e) {
      closeSession(session, CloseStatus.BAD_DATA, e.getMessage());
    } catch (Exception e) {
      closeSession(session, CloseStatus.SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  public void afterConnectionClosed(
      @NonNull WebSocketSession session,
      @NonNull CloseStatus status) {
    try {
      closeTerminalSessionUseCase.close(new CloseTerminalSessionCommand(
          session.getId()
      ));
    } finally {
      sessionRegistry.remove(session.getId());
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
