package net.happykoo.hcp.application;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.data.PodData;
import net.happykoo.hcp.domain.terminal.TerminalMessage;
import net.happykoo.hcp.domain.terminal.TerminalMessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstanceTerminalSessionServiceTest {

  @Mock
  private ExecuteTerminalCommandPort executeTerminalCommandPort;
  @Mock
  private ExecuteOrchestratorCommandPort executeOrchestratorCommandPort;

  @InjectMocks
  private InstanceTerminalSessionService instanceTerminalSessionService;

  @Test
  @DisplayName("터미널 세션/ssh key 작업 :: 포드 조회 후 각 명령을 위임")
  void terminalDelegationTest1() {
    UUID instanceId = UUID.randomUUID();
    when(executeOrchestratorCommandPort.executeGetPodInfoCommand(instanceId))
        .thenReturn(new PodData("default", "pod-1"));

    instanceTerminalSessionService.open(new OpenTerminalSessionCommand("session-1", instanceId));
    instanceTerminalSessionService.handle(new HandleTerminalBinaryInputCommand("session-1", new byte[]{1, 2}));
    instanceTerminalSessionService.resize(new TerminalMessage(
        TerminalMessageType.RESIZE,
        "session-1",
        instanceId.toString(),
        null,
        120,
        40
    ));
    instanceTerminalSessionService.registerInstanceSshKey(
        new RegisterInstanceSshKeyCommand(instanceId, "ssh-rsa AAAA")
    );
    instanceTerminalSessionService.close(new CloseTerminalSessionCommand("session-1"));

    verify(executeTerminalCommandPort).open("session-1", "default", "pod-1");
    verify(executeTerminalCommandPort).write("session-1", new byte[]{1, 2});
    verify(executeTerminalCommandPort).resize("session-1", 120, 40);
    verify(executeTerminalCommandPort).registerSshKey("default", "pod-1", "ssh-rsa AAAA");
    verify(executeTerminalCommandPort).close("session-1");
  }
}
