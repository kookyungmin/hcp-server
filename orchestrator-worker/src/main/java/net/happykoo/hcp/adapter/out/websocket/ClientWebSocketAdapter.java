package net.happykoo.hcp.adapter.out.websocket;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.websocket.session.TerminalWebSocketSessionRegistry;
import net.happykoo.hcp.application.port.out.SendTerminalCommandResultPort;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientWebSocketAdapter implements SendTerminalCommandResultPort {

  private final TerminalWebSocketSessionRegistry terminalWebSocketSessionRegistry;

  @Override
  public void send(String sessionId, TerminalMessage message) {
    WebSocketSession session = terminalWebSocketSessionRegistry.getSession(sessionId);
    if (session == null || !session.isOpen()) {
      return;
    }

    try {
      session.sendMessage(new TextMessage(new Gson().toJson(message)));
    } catch (Exception e) {
      throw new IllegalStateException("소켓 메세지 전송에 실패 했습니다.", e);
    }
  }

  @Override
  public void send(String sessionId, byte[] bytes) {
    WebSocketSession session = terminalWebSocketSessionRegistry.getSession(sessionId);
    if (session == null || !session.isOpen()) {
      return;
    }

    try {
      session.sendMessage(new BinaryMessage(bytes));
    } catch (Exception e) {
      throw new IllegalStateException("소켓 메세지 전송에 실패 했습니다.", e);
    }
  }

  @Override
  public void close(String sessionId) {
    WebSocketSession session = terminalWebSocketSessionRegistry.getSession(sessionId);
    if (session == null || !session.isOpen()) {
      return;
    }

    try {
      send(sessionId, TerminalMessage.close(sessionId));
      session.close();
    } catch (Exception ignored) {
    }
  }
}
