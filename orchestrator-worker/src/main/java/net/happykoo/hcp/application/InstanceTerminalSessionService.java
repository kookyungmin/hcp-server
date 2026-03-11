package net.happykoo.hcp.application;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.CloseTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.HandleTerminalBinaryInputUseCase;
import net.happykoo.hcp.application.port.in.OpenTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.ResizeTerminalSessionUseCase;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.data.PodData;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.terminal.TerminalMessage;

@UseCase
@RequiredArgsConstructor
public class InstanceTerminalSessionService implements OpenTerminalSessionUseCase,
    HandleTerminalBinaryInputUseCase, CloseTerminalSessionUseCase, ResizeTerminalSessionUseCase {

  private final ExecuteTerminalCommandPort executeTerminalCommandPort;
  private final ExecuteOrchestratorCommandPort executeOrchestratorCommandPort;

  @Override
  public void open(OpenTerminalSessionCommand command) {
    PodData podData = executeOrchestratorCommandPort.executeGetPodInfoCommand(command.instanceId());
    executeTerminalCommandPort.open(command.sessionId(), podData.namespace(), podData.podName());
  }

  @Override
  public void handle(HandleTerminalBinaryInputCommand command) {
    executeTerminalCommandPort.write(command.sessionId(), command.bytes());

  }

  @Override
  public void close(CloseTerminalSessionCommand command) {
    executeTerminalCommandPort.close(command.sessionId());
  }

  @Override
  public void resize(TerminalMessage message) {
    executeTerminalCommandPort.resize(
        message.sessionId(),
        message.cols(),
        message.rows()
    );
  }
}
