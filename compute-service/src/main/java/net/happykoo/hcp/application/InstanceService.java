package net.happykoo.hcp.application;

import static net.happykoo.hcp.domain.idempotency.IdempotencyCommandType.INSTANCE_PROVISIONING;
import static net.happykoo.hcp.domain.outbox.OutboxEventType.INSTANCE_PROVISIONING_EVENT;
import static net.happykoo.hcp.domain.outbox.OutboxStatus.PENDING;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.out.GeneratePayloadHashPort;
import net.happykoo.hcp.application.port.out.GetIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveInstanceInfoPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.ServerInstance;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.domain.outbox.payload.InstanceProvisioningEventPayload;
import net.happykoo.hcp.exception.IdempotencyConflictException;

@UseCase
@RequiredArgsConstructor
public class InstanceService implements ProvisionInstanceUseCase {

  private final GeneratePayloadHashPort generatePayloadHashPort;
  private final GetIdempotencyRequestPort getIdempotencyRequestPort;
  private final SaveIdempotencyRequestPort saveIdempotencyRequestPort;
  private final SaveInstanceInfoPort saveInstanceInfoPort;
  private final SaveOutboxEventPort saveOutboxEventPort;

  @Override
  @Transactional
  public void provisionInstance(ProvisionInstanceCommand command) {
    //Idempotency (멱등성 체크)
    Optional<IdempotencyRequest> oldIdempotencyRequestOpt = getIdempotencyRequestPort.getRequestByKey(
        command.ownerId(),
        command.idempotencyKey());
    var requestHash = generatePayloadHashPort.generateSha256Hash(command.payload());

    if (oldIdempotencyRequestOpt.isPresent()) {
      //payload hash 비교 후 payload가 달라졌으면 409 에러 발생
      if (!requestHash.equals(oldIdempotencyRequestOpt.get().getRequestHash())) {
        throw new IdempotencyConflictException();
      }
      //같은 요청이면 이미 처리 되었기에 return
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

  private String buildInstanceProvisioningEventPayload(
      ServerInstance instance) {
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
