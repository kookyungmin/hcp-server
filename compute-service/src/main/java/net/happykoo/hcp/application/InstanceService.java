package net.happykoo.hcp.application;

import static net.happykoo.hcp.domain.idempotency.IdempotencyCommandType.INSTANCE_PROVISIONING;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.out.GeneratePayloadHashPort;
import net.happykoo.hcp.application.port.out.GetIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.PublishInstanceEvent;
import net.happykoo.hcp.application.port.out.SaveIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveInstanceInfoPort;
import net.happykoo.hcp.common.web.annotation.UseCase;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.ServerInstance;
import net.happykoo.hcp.exception.IdempotencyConflictException;

@UseCase
@RequiredArgsConstructor
public class InstanceService implements ProvisionInstanceUseCase {

  private final GeneratePayloadHashPort generatePayloadHashPort;
  private final GetIdempotencyRequestPort getIdempotencyRequestPort;
  private final SaveIdempotencyRequestPort saveIdempotencyRequestPort;
  private final SaveInstanceInfoPort saveInstanceInfoPort;
  private final PublishInstanceEvent publishInstanceEvent;

  @Override
  @Transactional
  public void provisionInstance(ProvisionInstanceCommand command) {
    //Idempotency (멱등성 체크)
    Optional<IdempotencyRequest> oldIdempotencyRequestOpt = getIdempotencyRequestPort.getRequestByKey(
        command.ownerId(),
        command.idempotencyKey());
    var requestHash = generatePayloadHashPort.generateHash(command.payload());

    if (oldIdempotencyRequestOpt.isPresent()) {
      //payload hash 비교 후 payload가 달라졌으면 409 에러 발생
      if (!requestHash.equals(oldIdempotencyRequestOpt.get().getRequestHash())) {
        throw new IdempotencyConflictException();
      }
      //같은 요청이면 이미 처리 되었기에 return
      return;
    }

    //인스턴스 정보 저장
    var instanceInfo = new ServerInstance(
        UUID.randomUUID(),
        command.ownerId(),
        command.name(),
        command.tagSet(),
        command.image(),
        command.vpc(),
        command.spec(),
        command.storage(),
        InstanceStatus.PROVISIONING
    );
    saveInstanceInfoPort.saveInstanceInfo(instanceInfo);

    //event broker consume
    publishInstanceEvent.publishProvisionInstanceEvent(instanceInfo);

    //Idempotency 저장
    var idempotency = new IdempotencyRequest(
        command.ownerId(),
        command.idempotencyKey(),
        INSTANCE_PROVISIONING,
        requestHash,
        null
    );
    saveIdempotencyRequestPort.saveIdempotencyRequest(idempotency);
  }
}
