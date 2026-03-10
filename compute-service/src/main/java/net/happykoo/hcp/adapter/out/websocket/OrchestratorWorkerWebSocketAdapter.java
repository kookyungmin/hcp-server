package net.happykoo.hcp.adapter.out.websocket;

import com.google.gson.Gson;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.websocket.payload.WorkerTerminalMessage;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.SendTerminalCommandResultPort;
import net.happykoo.hcp.domain.terminal.TerminalSession;
import net.happykoo.hcp.infrastructure.properties.OrchestratorWorkerWebSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OrchestratorWorkerWebSocketProperties.class)
public class OrchestratorWorkerWebSocketAdapter implements ExecuteTerminalCommandPort {

  private final SendTerminalCommandResultPort sendTerminalCommandResultPort;
  private final StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
  private final OrchestratorWorkerWebSocketProperties orchestratorWorkerWebSocketProperties;
  private final Map<String, WebSocketSession> workerSessions = new ConcurrentHashMap<>();

  @Override
  public void openInstanceTerminal(TerminalSession terminalSession) {
    try {
      var headers = new WebSocketHttpHeaders();
      headers.add("X-Internal-Caller", "compute-service");
      headers.add("X-User-Id", terminalSession.getUserId().toString());
      headers.add("X-Roles", terminalSession.getJoinedRoles());

      var handler = new OrchestratorWorkerWebSocketHandler(
          terminalSession,
          sendTerminalCommandResultPort,
          this
      );
      var workerSession = webSocketClient.execute(
          handler,
          headers,
          URI.create(orchestratorWorkerWebSocketProperties.getUrl())
      ).join();
      var message = new Gson().toJson(WorkerTerminalMessage.open(
          terminalSession.getSessionId(),
          terminalSession.getInstanceId().toString()
      ));

      workerSessions.put(terminalSession.getSessionId(), workerSession);
      workerSession.sendMessage(new TextMessage(message));
    } catch (Exception e) {
      throw new IllegalStateException("웹소켓 연결을 실패했습니다.", e);
    }
  }

  @Override
  public void sendBinary(String sessionId, byte[] bytes) {
    try {
      WebSocketSession workerSession = workerSessions.get(sessionId);
      workerSession.sendMessage(new BinaryMessage(bytes));
    } catch (Exception e) {
      throw new IllegalStateException("바이너리 소켓 메시지 전송에 실패했습니다.", e);
    }
  }

  @Override
  public void close(String sessionId) {
    WebSocketSession workerSession = workerSessions.remove(sessionId);
    if (workerSession == null) {
      return;
    }

    try {
      if (workerSession.isOpen()) {
        var message = new Gson().toJson(WorkerTerminalMessage.close(
            sessionId
        ));
        workerSession.sendMessage(new TextMessage(message));
        workerSession.close();
      }
    } catch (Exception ignored) {
    }
  }

  @Override
  public void ping(String sessionId, String message) {
    //TODO: 후에 구현
  }

  void removeSession(String sessionId) {
    workerSessions.remove(sessionId);
  }
}
