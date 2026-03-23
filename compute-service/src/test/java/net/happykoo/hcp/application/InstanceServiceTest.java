package net.happykoo.hcp.application;

import static net.happykoo.hcp.domain.instance.InstanceStatus.PROVISIONING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.RUNNING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.STOPPED;
import static net.happykoo.hcp.domain.instance.InstanceStatus.STOPPING;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.INSTANCE_PROVISIONING_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.REGISTER_SSH_KEY_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.UPDATE_INSTANCE_NETWORK_POLICY_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.application.port.in.command.FindInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.FindPagedInstanceCommand;
import net.happykoo.hcp.application.port.in.command.GetNetworkPolicyCommand;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.SaveInstanceStatusCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceSpecCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceTagCommand;
import net.happykoo.hcp.application.port.in.command.UpdateNetworkPolicyCommand;
import net.happykoo.hcp.application.port.out.GeneratePayloadHashPort;
import net.happykoo.hcp.application.port.out.GetIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.GetInstanceInfoPort;
import net.happykoo.hcp.application.port.out.GetInstanceSshKeyPort;
import net.happykoo.hcp.application.port.out.GetNetworkPolicyPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveInstanceInfoPort;
import net.happykoo.hcp.application.port.out.SaveInstanceSshKeyPort;
import net.happykoo.hcp.application.port.out.SaveNetworkPolicyPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.application.port.out.data.UpdateInstanceStatusData;
import net.happykoo.hcp.domain.idempotency.IdempotencyCommandType;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;
import net.happykoo.hcp.domain.instance.InstanceImage;
import net.happykoo.hcp.domain.instance.InstanceSpec;
import net.happykoo.hcp.domain.instance.InstanceSshKey;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.InstanceStorage;
import net.happykoo.hcp.domain.instance.ServerInstance;
import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkPolicyType;
import net.happykoo.hcp.domain.network.NetworkVpc;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.exception.IdempotencyConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class InstanceServiceTest {

  @Mock
  private GeneratePayloadHashPort generatePayloadHashPort;
  @Mock
  private GetIdempotencyRequestPort getIdempotencyRequestPort;
  @Mock
  private SaveIdempotencyRequestPort saveIdempotencyRequestPort;
  @Mock
  private SaveInstanceInfoPort saveInstanceInfoPort;
  @Mock
  private SaveOutboxEventPort saveOutboxEventPort;
  @Mock
  private GetInstanceInfoPort getInstanceInfoPort;
  @Mock
  private GetInstanceSshKeyPort getInstanceSshKeyPort;
  @Mock
  private SaveInstanceSshKeyPort saveInstanceSshKeyPort;
  @Mock
  private GetNetworkPolicyPort getNetworkPolicyPort;
  @Mock
  private SaveNetworkPolicyPort saveNetworkPolicyPort;

  @InjectMocks
  private InstanceService instanceService;

  @Test
  @DisplayName("provisionInstance() :: 인스턴스와 outbox/idempotency 정보를 저장")
  void provisionInstanceTest1() {
    UUID ownerId = UUID.randomUUID();
    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(saveInstanceInfoPort.saveInstanceInfo(any(ServerInstance.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    instanceService.provisionInstance(new ProvisionInstanceCommand(
        ownerId,
        "web-1",
        "blue,prod",
        "img-ubuntu",
        "vpc-default",
        "spec-small",
        "SSD",
        50,
        "idem-1"
    ));

    ArgumentCaptor<ServerInstance> instanceCaptor = ArgumentCaptor.forClass(ServerInstance.class);
    ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
    ArgumentCaptor<IdempotencyRequest> idempotencyCaptor =
        ArgumentCaptor.forClass(IdempotencyRequest.class);

    verify(saveInstanceInfoPort).saveInstanceInfo(instanceCaptor.capture());
    verify(saveOutboxEventPort).saveOutboxEvent(outboxCaptor.capture());
    verify(saveIdempotencyRequestPort).saveIdempotencyRequest(idempotencyCaptor.capture());

    ServerInstance savedInstance = instanceCaptor.getValue();
    assertEquals(ownerId, savedInstance.getOwnerId());
    assertEquals("web-1", savedInstance.getName());
    assertEquals(PROVISIONING, savedInstance.getStatus());
    assertEquals(Set.of("blue", "prod"), savedInstance.getTags());

    OutboxEvent outboxEvent = outboxCaptor.getValue();
    assertEquals(INSTANCE_PROVISIONING_EVENT, outboxEvent.getEventType());
    assertNotNull(outboxEvent.getPayload());

    IdempotencyRequest idempotencyRequest = idempotencyCaptor.getValue();
    assertEquals(ownerId, idempotencyRequest.getOwnerId());
    assertEquals("idem-1", idempotencyRequest.getIdempotencyKey());
    assertEquals(IdempotencyCommandType.INSTANCE_PROVISIONING, idempotencyRequest.getCommandType());
    assertEquals("hash", idempotencyRequest.getRequestHash());
  }

  @Test
  @DisplayName("provisionInstance() :: 동일 요청 hash 가 이미 존재하면 아무 작업도 하지 않음")
  void provisionInstanceTest2() {
    UUID ownerId = UUID.randomUUID();
    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash"))
        .thenReturn(Optional.of(new IdempotencyRequest(
            ownerId,
            "idem-1",
            IdempotencyCommandType.INSTANCE_PROVISIONING,
            "hash",
            null
        )));

    instanceService.provisionInstance(new ProvisionInstanceCommand(
        ownerId, "web-1", null, "img", "vpc", "spec", "SSD", 50, "idem-1"
    ));

    verify(saveInstanceInfoPort, never()).saveInstanceInfo(any());
    verify(saveOutboxEventPort, never()).saveOutboxEvent(any());
    verify(saveIdempotencyRequestPort, never()).saveIdempotencyRequest(any());
  }

  @Test
  @DisplayName("updateInstanceSpec() :: 기존 스토리지보다 줄이려 하면 예외 발생")
  void updateInstanceSpecTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(getInstanceInfoPort.findInstanceWithAllById(instanceId)).thenReturn(instance(ownerId, instanceId));

    assertThrows(
        IllegalStateException.class,
        () -> instanceService.updateInstanceSpec(new UpdateInstanceSpecCommand(
            ownerId, instanceId, "idem-1", "spec-large", "SSD", 50))
    );

    verify(saveInstanceInfoPort, never()).saveInstanceInfo(any());
  }

  @Test
  @DisplayName("stopInstance() :: 실행 중 인스턴스를 STOPPING 으로 변경하고 상태 업데이트 이벤트를 남김")
  void stopInstanceTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    ServerInstance instance = instance(ownerId, instanceId);
    instance.updateStatus(RUNNING);

    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(getInstanceInfoPort.findInstanceById(instanceId)).thenReturn(instance);

    instanceService.stopInstance(new UpdateInstanceLifecycleCommand(instanceId, ownerId, "idem-1"));

    ArgumentCaptor<UpdateInstanceStatusData> statusCaptor =
        ArgumentCaptor.forClass(UpdateInstanceStatusData.class);
    ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);

    verify(saveInstanceInfoPort).updateInstanceStatus(statusCaptor.capture());
    verify(saveOutboxEventPort).saveOutboxEvent(outboxCaptor.capture());

    assertEquals(STOPPING, instance.getStatus());
    assertEquals(STOPPING, statusCaptor.getValue().status());
    assertEquals("UPDATE_INSTANCE_LIFECYCLE_EVENT", outboxCaptor.getValue().getEventType().name());
  }

  @Test
  @DisplayName("terminateInstance() :: 중지 상태가 아니면 예외 발생")
  void terminateInstanceTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    ServerInstance instance = instance(ownerId, instanceId);
    instance.updateStatus(RUNNING);

    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(getInstanceInfoPort.findInstanceById(instanceId)).thenReturn(instance);

    assertThrows(
        IllegalStateException.class,
        () -> instanceService.terminateInstance(new UpdateInstanceLifecycleCommand(
            instanceId, ownerId, "idem-1"))
    );
  }

  @Test
  @DisplayName("saveInstanceSshKey() :: 기존 키를 제거하고 새 키와 outbox/idempotency 를 저장")
  void saveInstanceSshKeyTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();

    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(getInstanceInfoPort.findInstanceById(instanceId)).thenReturn(instance(ownerId, instanceId));

    instanceService.saveInstanceSshKey(new RegisterInstanceSshKeyCommand(
        instanceId, ownerId, "idem-1", "main-key", "ssh-rsa AAAA"
    ));

    ArgumentCaptor<InstanceSshKey> sshKeyCaptor = ArgumentCaptor.forClass(InstanceSshKey.class);
    ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);

    verify(saveInstanceSshKeyPort).removeInstanceSshKey(instanceId);
    verify(saveInstanceSshKeyPort).saveInstanceSshKey(sshKeyCaptor.capture());
    verify(saveOutboxEventPort).saveOutboxEvent(outboxCaptor.capture());

    assertEquals("main-key", sshKeyCaptor.getValue().getName());
    assertEquals("ssh-rsa AAAA", sshKeyCaptor.getValue().getKey());
    assertEquals(REGISTER_SSH_KEY_EVENT, outboxCaptor.getValue().getEventType());
  }

  @Test
  @DisplayName("updateNetworkPolicy() :: 정책을 교체하고 outbox 이벤트를 저장")
  void updateNetworkPolicyTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    List<NetworkPolicy> policies = List.of(
        new NetworkPolicy(instanceId, NetworkPolicyType.INGRESS, "ssh", "0.0.0.0/0", "22")
    );

    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash")).thenReturn(Optional.empty());
    when(getInstanceInfoPort.findInstanceById(instanceId)).thenReturn(instance(ownerId, instanceId));

    instanceService.updateNetworkPolicy(new UpdateNetworkPolicyCommand(
        instanceId, ownerId, "idem-1", policies
    ));

    ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
    verify(saveNetworkPolicyPort).removeAllNetworkPolicyByInstanceId(instanceId);
    verify(saveNetworkPolicyPort).saveAllNetworkPolicy(policies);
    verify(saveOutboxEventPort).saveOutboxEvent(outboxCaptor.capture());
    assertEquals(UPDATE_INSTANCE_NETWORK_POLICY_EVENT, outboxCaptor.getValue().getEventType());
  }

  @Test
  @DisplayName("find/get/update 류 조회 메서드 :: 소유권을 확인하고 포트를 위임")
  void queryAndMutationMethodsTest1() {
    UUID ownerId = UUID.randomUUID();
    UUID otherId = UUID.randomUUID();
    UUID instanceId = UUID.randomUUID();
    ServerInstance instance = instance(ownerId, instanceId);
    InstanceSshKey sshKey = new InstanceSshKey(instanceId, "main-key", "ssh-rsa AAAA");
    List<NetworkPolicy> policies = List.of(
        new NetworkPolicy(instanceId, NetworkPolicyType.EGRESS, "https", "0.0.0.0/0", "443")
    );
    Page<ServerInstance> page = new PageImpl<>(List.of(instance));

    when(getInstanceInfoPort.findInstanceById(instanceId)).thenReturn(instance);
    when(getInstanceInfoPort.findInstanceWithAllById(instanceId)).thenReturn(instance);
    when(getInstanceSshKeyPort.findInstanceSshKey(instanceId)).thenReturn(sshKey);
    when(getNetworkPolicyPort.findAllNetworkPolicies(instanceId)).thenReturn(policies);
    when(getInstanceInfoPort.findPagedInstanceByOwnerIdAndSearchKeyword(ownerId, "web",
        PageRequest.of(0, 10))).thenReturn(page);

    assertEquals(instance, instanceService.findInstanceInfo(instanceId, ownerId));
    assertEquals(sshKey, instanceService.findInstanceSshKey(new FindInstanceSshKeyCommand(instanceId, ownerId)));
    assertEquals(policies, instanceService.getNetworkPolicy(new GetNetworkPolicyCommand(instanceId, ownerId)));
    assertEquals(page, instanceService.findPagedInstanceByOwnerIdAndSearchKeyword(
        new FindPagedInstanceCommand(ownerId, "web", PageRequest.of(0, 10))
    ));

    instanceService.updateInstanceTag(new UpdateInstanceTagCommand(ownerId, instanceId, "alpha,beta"));
    instanceService.saveInstanceStatus(new SaveInstanceStatusCommand(instanceId, STOPPED, "ok", "1.1.1.1", "10.0.0.1"));

    verify(saveInstanceInfoPort).saveInstanceInfo(instance);
    verify(saveInstanceInfoPort).updateInstanceStatus(any(UpdateInstanceStatusData.class));

    assertThrows(
        IllegalStateException.class,
        () -> instanceService.findInstanceInfo(instanceId, otherId)
    );
  }

  @Test
  @DisplayName("멱등성 확인 :: 저장된 request hash 가 다르면 conflict 예외 발생")
  void idempotencyConflictTest1() {
    UUID ownerId = UUID.randomUUID();
    when(generatePayloadHashPort.generateSha256Hash(any())).thenReturn("hash");
    when(getIdempotencyRequestPort.findRequestByKey(ownerId, "hash"))
        .thenReturn(Optional.of(new IdempotencyRequest(
            ownerId,
            "idem-1",
            IdempotencyCommandType.INSTANCE_PROVISIONING,
            "different-hash",
            null
        )));

    assertThrows(
        IdempotencyConflictException.class,
        () -> instanceService.provisionInstance(new ProvisionInstanceCommand(
            ownerId, "web-1", null, "img", "vpc", "spec", "SSD", 50, "idem-1"))
    );
  }

  private ServerInstance instance(UUID ownerId, UUID instanceId) {
    return new ServerInstance(
        instanceId,
        ownerId,
        "web-1",
        new HashSet<>(Set.of("prod")),
        new InstanceImage("img-1", "ubuntu", "Ubuntu", "Ubuntu", "24.04"),
        new NetworkVpc(
            "vpc-1",
            "default",
            "Default VPC",
            DefaultNetworkPolicy.ALLOW_ALL,
            DefaultNetworkPolicy.ALLOW_SAME_VPC,
            "10.0.0.0/24"
        ),
        new InstanceSpec("spec-1", "small", "Small", "2", "4Gi"),
        new InstanceStorage("SSD", 100),
        PROVISIONING
    );
  }
}
