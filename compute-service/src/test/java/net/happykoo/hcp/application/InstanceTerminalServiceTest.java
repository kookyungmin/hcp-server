package net.happykoo.hcp.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.application.port.in.command.CloseTerminalSessionCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalBinaryInputCommand;
import net.happykoo.hcp.application.port.in.command.HandleTerminalControlCommand;
import net.happykoo.hcp.application.port.in.command.OpenTerminalSessionCommand;
import net.happykoo.hcp.application.port.out.ExecuteTerminalCommandPort;
import net.happykoo.hcp.application.port.out.GetInstanceInfoPort;
import net.happykoo.hcp.application.port.out.GetTerminalSessionPort;
import net.happykoo.hcp.application.port.out.SaveTerminalSessionPort;
import net.happykoo.hcp.domain.terminal.TerminalSession;
import net.happykoo.hcp.domain.terminal.TerminalUserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstanceTerminalServiceTest {

  @Mock
  private GetInstanceInfoPort getInstanceInfoPort;
  @Mock
  private GetTerminalSessionPort getTerminalSessionPort;
  @Mock
  private SaveTerminalSessionPort saveTerminalSessionPort;
  @Mock
  private ExecuteTerminalCommandPort executeTerminalCommandPort;

  @InjectMocks
  private InstanceTerminalService instanceTerminalService;

  @Test
  @DisplayName("open() :: 인스턴스 소유주가 아니면 예외 발생")
  void openTest1() {
    UUID instanceId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(getInstanceInfoPort.existsByInstanceId(userId, instanceId)).thenReturn(false);

    assertThrows(
        IllegalStateException.class,
        () -> instanceTerminalService.open(
            new OpenTerminalSessionCommand("session-1", instanceId, userId, List.of("ROLE_USER")))
    );

    verify(saveTerminalSessionPort, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("open() :: 권한이 있으면 세션을 저장하고 터미널을 연다")
  void openTest2() {
    UUID instanceId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(getInstanceInfoPort.existsByInstanceId(userId, instanceId)).thenReturn(true);

    instanceTerminalService.open(
        new OpenTerminalSessionCommand("session-1", instanceId, userId, List.of("ROLE_USER"))
    );

    verify(saveTerminalSessionPort).save(org.mockito.ArgumentMatchers.any(TerminalSession.class));
    verify(executeTerminalCommandPort).openInstanceTerminal(
        org.mockito.ArgumentMatchers.any(TerminalSession.class)
    );
  }

  @Test
  @DisplayName("handle(binary) :: 세션이 없으면 예외 발생")
  void handleBinaryTest1() {
    when(getTerminalSessionPort.findSessionById("session-1")).thenReturn(null);

    assertThrows(
        IllegalStateException.class,
        () -> instanceTerminalService.handle(
            new HandleTerminalBinaryInputCommand("session-1", new byte[]{1, 2}))
    );
  }

  @Test
  @DisplayName("handle(binary) :: 세션이 있으면 바이너리 데이터를 전달")
  void handleBinaryTest2() {
    when(getTerminalSessionPort.findSessionById("session-1")).thenReturn(
        new TerminalSession("session-1", UUID.randomUUID(),
            new TerminalUserContext(UUID.randomUUID(), List.of("ROLE_USER")))
    );

    instanceTerminalService.handle(new HandleTerminalBinaryInputCommand("session-1", new byte[]{1, 2}));

    verify(executeTerminalCommandPort).sendBinary("session-1", new byte[]{1, 2});
  }

  @Test
  @DisplayName("handle(control) :: resize 메시지면 resize 를 호출")
  void handleControlTest1() {
    when(getTerminalSessionPort.findSessionById("session-1")).thenReturn(
        new TerminalSession("session-1", UUID.randomUUID(),
            new TerminalUserContext(UUID.randomUUID(), List.of("ROLE_USER")))
    );

    instanceTerminalService.handle(new HandleTerminalControlCommand(
        "session-1",
        "{\"type\":\"RESIZE\",\"cols\":120,\"rows\":40}"
    ));

    verify(executeTerminalCommandPort).resize("session-1", 120, 40);
  }

  @Test
  @DisplayName("handle(control) :: close 메시지면 세션을 종료")
  void handleControlTest2() {
    when(getTerminalSessionPort.findSessionById("session-1")).thenReturn(
        new TerminalSession("session-1", UUID.randomUUID(),
            new TerminalUserContext(UUID.randomUUID(), List.of("ROLE_USER")))
    );

    instanceTerminalService.handle(new HandleTerminalControlCommand(
        "session-1",
        "{\"type\":\"CLOSE\"}"
    ));

    verify(executeTerminalCommandPort).close("session-1");
    verify(saveTerminalSessionPort).remove("session-1");
  }

  @Test
  @DisplayName("close() :: 터미널 종료와 세션 삭제를 수행")
  void closeTest1() {
    instanceTerminalService.close(new CloseTerminalSessionCommand("session-1"));

    verify(executeTerminalCommandPort).close("session-1");
    verify(saveTerminalSessionPort).remove("session-1");
  }
}
