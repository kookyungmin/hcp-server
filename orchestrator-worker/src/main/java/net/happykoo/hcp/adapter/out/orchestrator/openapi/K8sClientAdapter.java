package net.happykoo.hcp.adapter.out.orchestrator.openapi;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import java.io.InputStream;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.SendTerminalCommandResultPort;
import net.happykoo.hcp.common.annotation.OrchestratorAdapter;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import net.happykoo.hcp.infrastructure.properties.K8sProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OrchestratorAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(K8sProperties.class)
@Slf4j
public class K8sClientAdapter implements ExecuteTerminalCommandPort {

  private final ApiClient apiClient;
  private final K8sTerminalSessionRegistry terminalSessionRegistry;
  private final SendTerminalCommandResultPort sendTerminalCommandResultPort;
  private final K8sProperties k8sProperties;

  @Override
  public void open(
      String sessionId,
      String namespace,
      String podName
  ) {
    try {
      Exec exec = new Exec(apiClient);
      Exec.ExecProcess process = (Exec.ExecProcess) exec.exec(
          namespace,
          podName,
          new String[]{
              "/bin/bash",
              "-il"
//              "-c",
//              "su - " + k8sProperties.getUserName()
          },
          null,
          true,
          true
      );
      K8sTerminalSession session = new K8sTerminalSession(
          process,
          process.getOutputStream(),
          process.getInputStream(),
          process.getErrorStream()
      );
      terminalSessionRegistry.register(sessionId, session);
      sendTerminalCommandResultPort.send(sessionId, TerminalMessage.open(sessionId));

      startPump(sessionId, session.stdout());
      startPump(sessionId, session.stderr());
      startWaiter(sessionId, process);
    } catch (Exception e) {
      throw new IllegalStateException("세션을 여는데 실패했습니다.", e);
    }
  }

  @Override
  public void write(String sessionId, byte[] bytes) {
    K8sTerminalSession session = requireSession(sessionId);

    try {
      session.stdin().write(bytes);
      session.stdin().flush();
    } catch (Exception e) {
      throw new IllegalStateException("스트림 데이터 전송 중 에러가 발생했습니다.");
    }
  }

  @Override
  public void resize(String sessionId, Integer cols, Integer rows) {
    K8sTerminalSession session = requireSession(sessionId);

    if (cols == null || rows == null || cols < 1 || rows < 1) {
      return;
    }

    try {
      session.process().resize(cols, rows);
    } catch (Exception e) {
      throw new IllegalStateException("터미널 크기 변경 중 에러가 발생했습니다.", e);
    }
  }

  @Override
  public void close(String sessionId) {
    K8sTerminalSession session = terminalSessionRegistry.remove(sessionId);
    if (session != null) {
      session.close();
    }
    sendTerminalCommandResultPort.send(sessionId, TerminalMessage.close(sessionId));
  }

  private void startWaiter(String sessionId, Process process) {
    Thread.ofVirtual().start(() -> {
      try {
        process.waitFor();
      } catch (InterruptedException ignored) {
        log.error(ignored.getMessage());
      } finally {
        sendTerminalCommandResultPort.close(sessionId);
        close(sessionId);
      }
    });
  }

  private void startPump(String sessionId, InputStream inputStream) {
    Thread.ofVirtual().start(() -> {
      byte[] buffer = new byte[8192];

      try {
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
          byte[] copy = Arrays.copyOf(buffer, read);
          sendTerminalCommandResultPort.send(sessionId, copy);
        }
      } catch (Exception e) {
        sendTerminalCommandResultPort.send(
            sessionId,
            TerminalMessage.error("스트림 데이터 전송 중 에러가 발생했습니다.")
        );
      }
    });
  }

  private K8sTerminalSession requireSession(String sessionId) {
    K8sTerminalSession session = terminalSessionRegistry.get(sessionId);
    if (session == null) {
      throw new IllegalStateException("명령을 실행할 세션이 존재하지 않습니다.");
    }
    return session;
  }
}
