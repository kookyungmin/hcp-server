package net.happykoo.hcp.application;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.CloseTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalBinaryInputUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalControlUseCase;
import net.happykoo.hcp.application.port.in.OpenTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalControlCommand;
import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.GetInstanceInfoPort;
import net.happykoo.hcp.application.port.out.GetTerminalSessionPort;
import net.happykoo.hcp.application.port.out.SaveTerminalSessionPort;
import net.happykoo.hcp.application.port.out.SendTerminalCommandResultPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import net.happykoo.hcp.domain.terminal.TerminalSession;
import net.happykoo.hcp.domain.terminal.TerminalUserContext;

@UseCase
@RequiredArgsConstructor
public class InstanceTerminalService implements OpenTerminalSessionUseCase,
    CloseTerminalSessionUseCase, HandleTerminalBinaryInputUseCase, HandleTerminalControlUseCase {

  private final GetInstanceInfoPort getInstanceInfoPort;
  private final GetTerminalSessionPort getTerminalSessionPort;
  private final SaveTerminalSessionPort saveTerminalSessionPort;
  private final ExecuteTerminalCommandPort executeTerminalCommandPort;
  private final SendTerminalCommandResultPort sendTerminalCommandResultPort;

  @Override
  public void open(OpenTerminalSessionCommand command) {
    TerminalSession terminalSession = new TerminalSession(
        command.sessionId(),
        command.instanceId(),
        new TerminalUserContext(command.userId(), command.roles())
    );

    //인스턴스 소유주 확인
    if (!getInstanceInfoPort.existsByInstanceId(terminalSession.getUserId(),
        terminalSession.getInstanceId())) {
      throw new IllegalStateException("인스턴스 접근 권한이 없습니다.");
    }

    saveTerminalSessionPort.save(terminalSession);
    executeTerminalCommandPort.openInstanceTerminal(terminalSession);
    sendTerminalCommandResultPort.send(terminalSession.getSessionId(),
        TerminalMessage.ready());
  }

  @Override
  public void handle(HandleTerminalBinaryInputCommand command) {
    TerminalSession terminalSession = getTerminalSessionPort.findSessionById(command.sessionId());
    if (terminalSession == null) {
      throw new IllegalStateException("연결된 세션이 없습니다.");
    }
    executeTerminalCommandPort.sendBinary(command.sessionId(), command.bytes());
  }

  @Override
  public void handle(HandleTerminalControlCommand command) {
    TerminalSession terminalSession = getTerminalSessionPort.findSessionById(command.sessionId());
    if (terminalSession == null) {
      throw new IllegalStateException("연결된 세션이 없습니다.");
    }
    TerminalMessage terminalMessage = new Gson().fromJson(command.message(), TerminalMessage.class);
    switch (terminalMessage.type()) {
      case RESIZE -> executeTerminalCommandPort.resizeTerminal(
          command.sessionId(),
          terminalMessage.cols(),
          terminalMessage.rows()
      );
      case PING -> executeTerminalCommandPort.ping(
          command.sessionId(),
          terminalMessage.message()
      );
      case CLOSE -> close(new CloseTerminalSessionCommand(command.sessionId()));
    }
  }

  @Override
  public void close(CloseTerminalSessionCommand command) {
    executeTerminalCommandPort.close(command.sessionId());
    sendTerminalCommandResultPort.close(command.sessionId());
    saveTerminalSessionPort.remove(command.sessionId());
  }
}
