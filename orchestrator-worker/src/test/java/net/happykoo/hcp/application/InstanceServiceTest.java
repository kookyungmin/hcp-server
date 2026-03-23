package net.happykoo.hcp.application;

import static net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult.ACQUIRED;
import static net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult.ALREADY_DONE;
import static net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult.BUSY;
import static net.happykoo.hcp.application.port.out.data.InstanceStatusData.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.ScaleInstanceCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
import net.happykoo.hcp.application.port.in.command.UpdateNetworkPolicyCommand;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.PublishInstanceStatusEventPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyPort;
import net.happykoo.hcp.application.port.out.TryLockPort;
import net.happykoo.hcp.domain.idempotency.Idempotency;
import net.happykoo.hcp.domain.idempotency.IdempotencyStatus;
import net.happykoo.hcp.domain.instance.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.instance.Instance;
import net.happykoo.hcp.domain.instance.InstanceNetworkPolicy;
import net.happykoo.hcp.domain.instance.NetworkPolicyType;
import net.happykoo.hcp.exception.RetryableException;
import net.happykoo.hcp.infrastructure.properties.IdempotencyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstanceServiceTest {

  @Mock
  private ExecuteOrchestratorCommandPort executeOrchestratorCommandPort;
  @Mock
  private SaveIdempotencyPort saveIdempotencyPort;
  @Mock
  private PublishInstanceStatusEventPort publishInstanceStatusEventPort;
  @Mock
  private TryLockPort tryLockPort;

  private IdempotencyProperties idempotencyProperties;

  @InjectMocks
  private InstanceService instanceService;

  @BeforeEach
  void setUp() {
    idempotencyProperties = new IdempotencyProperties();
    idempotencyProperties.setProcessingTtlSeconds(30L);
    instanceService = new InstanceService(
        executeOrchestratorCommandPort,
        saveIdempotencyPort,
        idempotencyProperties,
        publishInstanceStatusEventPort,
        tryLockPort
    );
    lenient().doAnswer(invocation -> {
      Runnable runnable = invocation.getArgument(1);
      runnable.run();
      return null;
    }).when(tryLockPort).tryLockInstance(any(UUID.class), any(Runnable.class));
  }

  @Test
  @DisplayName("provisionInstance() :: 멱등성 선점 성공 시 오케스트레이터 명령을 실행하고 SUCCESS 저장")
  void provisionInstanceTest1() {
    UUID eventId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    when(saveIdempotencyPort.tryAcquireIdempotency(any(Idempotency.class))).thenReturn(ACQUIRED);

    instanceService.provisionInstance(new ProvisionInstanceCommand(
        eventId,
        instanceId,
        ownerId,
        "ubuntu-24",
        "allow_all",
        "deny_all",
        "10.0.0.0/24",
        "2",
        "4Gi",
        "SSD",
        50
    ));

    ArgumentCaptor<Instance> instanceCaptor = ArgumentCaptor.forClass(Instance.class);
    ArgumentCaptor<Idempotency> idempotencyCaptor = ArgumentCaptor.forClass(Idempotency.class);

    verify(executeOrchestratorCommandPort).executeProvisionInstanceCommand(instanceCaptor.capture());
    verify(saveIdempotencyPort).saveIdempotency(idempotencyCaptor.capture());

    assertEquals(instanceId, instanceCaptor.getValue().getInstanceId());
    assertEquals(ownerId, instanceCaptor.getValue().getOwnerId());
    assertEquals(DefaultNetworkPolicy.ALLOW_ALL, instanceCaptor.getValue().getDefaultEgressPolicy());
    assertEquals(IdempotencyStatus.SUCCESS, idempotencyCaptor.getValue().getStatus());
  }

  @Test
  @DisplayName("stopInstance() :: BUSY 상태면 retry 예외 발생")
  void stopInstanceTest1() {
    UUID eventId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    when(saveIdempotencyPort.tryAcquireIdempotency(any(Idempotency.class))).thenReturn(BUSY);

    assertThrows(
        RetryableException.class,
        () -> instanceService.stopInstance(new UpdateInstanceLifecycleCommand(
            eventId, instanceId, UUID.randomUUID()))
    );

    verify(executeOrchestratorCommandPort, never()).executeStopInstanceCommand(any());
    verify(saveIdempotencyPort, never()).saveIdempotency(any());
  }

  @Test
  @DisplayName("restartInstance() :: 이미 완료된 작업이면 명령 실행 없이 종료")
  void restartInstanceTest1() {
    when(saveIdempotencyPort.tryAcquireIdempotency(any(Idempotency.class))).thenReturn(ALREADY_DONE);

    instanceService.restartInstance(new UpdateInstanceLifecycleCommand(
        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
    ));

    verify(executeOrchestratorCommandPort, never()).executeRestartInstanceCommand(any());
    verify(saveIdempotencyPort, never()).saveIdempotency(any());
  }

  @Test
  @DisplayName("terminateInstance() :: 오케스트레이터 명령 실패 시 FAILED 저장")
  void terminateInstanceTest1() {
    UUID eventId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    when(saveIdempotencyPort.tryAcquireIdempotency(any(Idempotency.class))).thenReturn(ACQUIRED);
    org.mockito.Mockito.doThrow(new IllegalStateException("boom"))
        .when(executeOrchestratorCommandPort)
        .executeTerminateInstanceCommand(instanceId);

    assertThrows(
        IllegalStateException.class,
        () -> instanceService.terminateInstance(new UpdateInstanceLifecycleCommand(
            eventId, instanceId, UUID.randomUUID()))
    );

    ArgumentCaptor<Idempotency> captor = ArgumentCaptor.forClass(Idempotency.class);
    verify(saveIdempotencyPort).saveIdempotency(captor.capture());
    assertEquals(IdempotencyStatus.FAILED, captor.getValue().getStatus());
  }

  @Test
  @DisplayName("watchStatusAndSendEvent() :: 조회한 상태를 그대로 publish")
  void watchStatusAndSendEventTest1() {
    UUID instanceId = UUID.randomUUID();
    when(executeOrchestratorCommandPort.executeGetInstanceStatusCommand(instanceId))
        .thenReturn(success(instanceId.toString(), "1.1.1.1", "10.0.0.1"));

    instanceService.watchStatusAndSendEvent(instanceId);

    verify(publishInstanceStatusEventPort).publishInstanceStatusEvent(
        success(instanceId.toString(), "1.1.1.1", "10.0.0.1")
    );
  }

  @Test
  @DisplayName("scale/updateNetworkPolicy() :: 인스턴스/정책 정보를 전달")
  void scalingAndPolicyTest1() {
    UUID eventId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    when(saveIdempotencyPort.tryAcquireIdempotency(any(Idempotency.class))).thenReturn(ACQUIRED);

    instanceService.scaleInstance(new ScaleInstanceCommand(
        eventId, instanceId, ownerId, "4", "8Gi", "SSD", 100
    ));
    instanceService.updateNetworkPolicy(new UpdateNetworkPolicyCommand(
        eventId,
        instanceId,
        List.of(new InstanceNetworkPolicy(
            instanceId, NetworkPolicyType.INGRESS, "ssh", "0.0.0.0/0", "22"))
    ));

    verify(executeOrchestratorCommandPort).executeScaleInstanceCommand(any(Instance.class));
    verify(executeOrchestratorCommandPort).executeUpdateNetworkPolicyCommand(any(UUID.class), any());
  }
}
