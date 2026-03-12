package net.happykoo.hcp.application;

import static net.happykoo.hcp.domain.idempotency.IdempotencyCommandType.INSTANCE_PROVISIONING;
import static net.happykoo.hcp.domain.idempotency.IdempotencyCommandType.INSTANCE_SCALING;
import static net.happykoo.hcp.domain.idempotency.IdempotencyCommandType.UPDATE_INSTANCE_LIFECYCLE;
import static net.happykoo.hcp.domain.instance.InstanceStatus.RESTARTING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.RUNNING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.STOPPED;
import static net.happykoo.hcp.domain.instance.InstanceStatus.STOPPING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.TERMINATING;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.INSTANCE_PROVISIONING_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.INSTANCE_SCALING_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.UPDATE_INSTANCE_LIFECYCLE_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxStatus.PENDING;

import com.google.gson.Gson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.FindInstanceUseCase;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.SaveInstanceStatusUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceLifecycleUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceSpecUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceTagUseCase;
import net.happykoo.hcp.application.port.in.command.FindPagedInstanceCommand;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.SaveInstanceStatusCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceSpecCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceTagCommand;
import net.happykoo.hcp.application.port.out.GeneratePayloadHashPort;
import net.happykoo.hcp.application.port.out.GetIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.GetInstanceInfoPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveInstanceInfoPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.application.port.out.data.UpdateInstanceStatusData;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;
import net.happykoo.hcp.domain.instance.InstanceSpec;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.InstanceStorage;
import net.happykoo.hcp.domain.instance.ServerInstance;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.domain.outbox.payload.InstanceProvisioningEventPayload;
import net.happykoo.hcp.domain.outbox.payload.InstanceScalingEventPayload;
import net.happykoo.hcp.domain.outbox.payload.InstanceUpdateLifecycleEventPayload;
import net.happykoo.hcp.exception.IdempotencyConflictException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class InstanceService implements ProvisionInstanceUseCase,
    SaveInstanceStatusUseCase, FindInstanceUseCase, UpdateInstanceLifecycleUseCase,
    UpdateInstanceTagUseCase, UpdateInstanceSpecUseCase {

  private final GeneratePayloadHashPort generatePayloadHashPort;
  private final GetIdempotencyRequestPort getIdempotencyRequestPort;
  private final SaveIdempotencyRequestPort saveIdempotencyRequestPort;
  private final SaveInstanceInfoPort saveInstanceInfoPort;
  private final SaveOutboxEventPort saveOutboxEventPort;
  private final GetInstanceInfoPort getInstanceInfoPort;

  @Override
  @Transactional
  public void provisionInstance(ProvisionInstanceCommand command) {
    //Idempotency (멱등성 체크)
    var requestHash = generatePayloadHashPort.generateSha256Hash(command.payload());
    if (!tryAcquireIdempotency(
        command.ownerId(),
        requestHash
    )) {
      return;
    }

    //인스턴스 정보 저장
    var instance = saveInstanceInfoPort.saveInstanceInfo(new ServerInstance(
        UUID.randomUUID(),
        command.ownerId(),
        command.name(),
        command.tagSet(),
        command.image(),
        command.vpc(),
        command.spec(),
        command.storage(),
        InstanceStatus.PROVISIONING
    ));

    //outbox(for event broker) 저장
    saveOutboxEventPort.saveOutboxEvent(new OutboxEvent(
        UUID.randomUUID(),
        INSTANCE_PROVISIONING_EVENT,
        buildInstanceProvisioningEventPayload(instance),
        PENDING,
        0
    ));

    //Idempotency 저장
    saveIdempotencyRequestPort.saveIdempotencyRequest(new IdempotencyRequest(
        command.ownerId(),
        command.idempotencyKey(),
        INSTANCE_PROVISIONING,
        requestHash,
        null
    ));
  }

  @Override
  public ServerInstance findInstanceInfo(UUID instanceId, UUID requesterId) {
    var instance = getInstanceInfoPort.findInstanceById(instanceId);
    if (!instance.getOwnerId().equals(requesterId)) {
      throw new IllegalStateException("인스턴스 접근 권한이 없습니다.");

    }
    return instance;
  }

  @Override
  public void saveInstanceStatus(SaveInstanceStatusCommand command) {
    saveInstanceInfoPort.updateInstanceStatus(new UpdateInstanceStatusData(
        command.instanceId(),
        command.status(),
        command.failureReason(),
        command.publicIp(),
        command.privateIp()
    ));
  }

  @Override
  public Page<ServerInstance> findPagedInstanceByOwnerIdAndSearchKeyword(
      FindPagedInstanceCommand command
  ) {
    return getInstanceInfoPort.findPagedInstanceByOwnerIdAndSearchKeyword(
        command.ownerId(),
        command.searchKeyword(),
        command.pageable()
    );
  }

  @Override
  @Transactional
  public void stopInstance(UpdateInstanceLifecycleCommand command) {
    updateInstanceLifecycle(command, InstanceStatus.STOPPING);
  }

  @Override
  @Transactional
  public void restartInstance(UpdateInstanceLifecycleCommand command) {
    updateInstanceLifecycle(command, InstanceStatus.RESTARTING);

  }

  @Override
  @Transactional
  public void terminateInstance(UpdateInstanceLifecycleCommand command) {
    updateInstanceLifecycle(command, TERMINATING);
  }

  @Override
  @Transactional
  public void updateInstanceSpec(
      UpdateInstanceSpecCommand command
  ) {
    //Idempotency (멱등성 체크)
    var requestHash = generatePayloadHashPort.generateSha256Hash(command.payload());
    if (!tryAcquireIdempotency(
        command.requesterId(),
        requestHash
    )) {
      return;
    }

    var instance = getInstanceInfoPort.findInstanceWithAllById(command.instanceId());
    if (!instance.getOwnerId().equals(command.requesterId())) {
      throw new IllegalStateException("인스턴스 접근 권한이 없습니다.");
    }

    if (instance.getStorageSize() > command.storageSize()) {
      throw new IllegalStateException("데이터 유실로 인해 스토리지는 증설만 가능합니다.");
    }

    instance.changeSpec(new InstanceSpec(
        command.specCode()
    ));

    instance.changeStorage(new InstanceStorage(
        command.storageType(),
        command.storageSize()
    ));

    var changedInstance = saveInstanceInfoPort.saveInstanceInfo(instance);

    //outbox(for event broker) 저장
    saveOutboxEventPort.saveOutboxEvent(new OutboxEvent(
        UUID.randomUUID(),
        INSTANCE_SCALING_EVENT,
        buildInstanceScalingEventPayload(changedInstance),
        PENDING,
        0
    ));

    //Idempotency 저장
    saveIdempotencyRequestPort.saveIdempotencyRequest(new IdempotencyRequest(
        command.requesterId(),
        command.idempotencyKey(),
        INSTANCE_SCALING,
        requestHash,
        null
    ));
  }

  @Override
  public void updateInstanceTag(UpdateInstanceTagCommand command) {
    var instance = getInstanceInfoPort.findInstanceWithAllById(command.instanceId());
    if (!instance.getOwnerId().equals(command.requesterId())) {
      throw new IllegalStateException("인스턴스 접근 권한이 없습니다.");
    }
    instance.clearTags();
    instance.addAllTags(command.tagSet());
    saveInstanceInfoPort.saveInstanceInfo(instance);
  }

  private void updateInstanceLifecycle(
      UpdateInstanceLifecycleCommand command,
      InstanceStatus toBeStatus
  ) {
    var requestHash = generatePayloadHashPort.generateSha256Hash(
        command.payload() + toBeStatus.name());
    if (!tryAcquireIdempotency(
        command.requesterId(),
        requestHash
    )) {
      return;
    }

    var instance = getInstanceInfoPort.findInstanceById(command.instanceId());

    if (!instance.getOwnerId().equals(command.requesterId())) {
      throw new IllegalStateException("인스턴스 접근 권한이 없습니다.");
    }

    //인스턴스 상태 체크
    checkInstanceStatusForLifecycle(instance, toBeStatus);

    //인스턴스 메타 상태 변경
    saveInstanceStatus(new SaveInstanceStatusCommand(
        instance.getInstanceId(),
        toBeStatus
    ));

    //outbox
    //outbox(for event broker) 저장
    saveOutboxEventPort.saveOutboxEvent(new OutboxEvent(
        UUID.randomUUID(),
        UPDATE_INSTANCE_LIFECYCLE_EVENT,
        buildInstanceLifecycleEventPayload(instance, toBeStatus),
        PENDING,
        0
    ));

    //Idempotency 저장
    {
      saveIdempotencyRequestPort.saveIdempotencyRequest(new IdempotencyRequest(
          command.requesterId(),
          command.idempotencyKey(),
          UPDATE_INSTANCE_LIFECYCLE,
          requestHash,
          null
      ));
    }
  }

  private void checkInstanceStatusForLifecycle(
      ServerInstance instance,
      InstanceStatus toBeStatus
  ) {
    if (toBeStatus.equals(TERMINATING) &&
        !List.of(STOPPED, TERMINATING).contains(instance.getStatus())) {
      throw new IllegalStateException("인스턴스가 중지 상태이어야 합니다.");
    }

    if (toBeStatus.equals(InstanceStatus.STOPPING) &&
        !List.of(RUNNING, STOPPING).contains(instance.getStatus())) {
      throw new IllegalStateException("인스턴스가 실행 상태이어야 합니다.");
    }

    if (toBeStatus.equals(InstanceStatus.RESTARTING) &&
        !List.of(STOPPED, RESTARTING).contains(instance.getStatus())) {
      throw new IllegalStateException("인스턴스가 중지 상태이어야 합니다.");
    }
  }

  private boolean tryAcquireIdempotency(
      UUID ownerId,
      String requestHash
  ) {
    Optional<IdempotencyRequest> oldIdempotencyRequestOpt = getIdempotencyRequestPort.findRequestByKey(
        ownerId,
        requestHash);

    if (oldIdempotencyRequestOpt.isPresent()) {
      //payload hash 비교 후 payload가 달라졌으면 409 에러 발생
      if (!requestHash.equals(oldIdempotencyRequestOpt.get().getRequestHash())) {
        throw new IdempotencyConflictException();
      }
      //같은 요청이면 이미 처리 되었기에 return
      return false;
    }
    return true;
  }

  private String buildInstanceLifecycleEventPayload(
      ServerInstance instance,
      InstanceStatus status
  ) {
    return new Gson().toJson(new InstanceUpdateLifecycleEventPayload(
        instance.getInstanceId().toString(),
        instance.getOwnerId().toString(),
        status
    ));
  }

  private String buildInstanceScalingEventPayload(
      ServerInstance instance
  ) {
    return new Gson().toJson(new InstanceScalingEventPayload(
        instance.getInstanceId().toString(),
        instance.getOwnerId().toString(),
        instance.getCpu(),
        instance.getMemory(),
        instance.getStorageType(),
        instance.getStorageSize()
    ));
  }

  private String buildInstanceProvisioningEventPayload(
      ServerInstance instance
  ) {
    return new Gson().toJson(new InstanceProvisioningEventPayload(
        instance.getInstanceId().toString(),
        instance.getOwnerId().toString(),
        instance.getImageName(),
        instance.getDefaultEgressPolicy(),
        instance.getDefaultIngressPolicy(),
        instance.getCidrBlock(),
        instance.getCpu(),
        instance.getMemory(),
        instance.getStorageType(),
        instance.getStorageSize()
    ));
  }
}
