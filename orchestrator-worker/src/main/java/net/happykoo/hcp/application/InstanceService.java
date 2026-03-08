package net.happykoo.hcp.application;

import static net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult.ALREADY_DONE;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyPort;
import net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.idempotency.Idempotency;
import net.happykoo.hcp.domain.idempotency.IdempotencyStatus;
import net.happykoo.hcp.domain.instance.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.instance.Instance;
import net.happykoo.hcp.exception.RetryableException;
import net.happykoo.hcp.infrastructure.properties.IdempotencyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@UseCase
@RequiredArgsConstructor
@EnableConfigurationProperties(IdempotencyProperties.class)
public class InstanceService implements ProvisionInstanceUseCase {

  private final ExecuteOrchestratorCommandPort executeOrchestratorCommandPort;
  private final SaveIdempotencyPort saveIdempotencyPort;
  private final IdempotencyProperties idempotencyProperties;

  @Override
  public void provisionInstance(
      ProvisionInstanceCommand command
  ) {
    //멱등성 선점 시도
    var idempotency = new Idempotency(
        command.eventId(),
        IdempotencyStatus.PROCESSING,
        Instant.now().plusSeconds(idempotencyProperties.getProcessingTtlSeconds())
    );
    IdempotencyAcquireResult acquireResult = saveIdempotencyPort.tryAcquireIdempotency(idempotency);
    if (acquireResult == ALREADY_DONE) {
      //이미 완료된 작업이므로 return 해서 ack commit
      return;
    }
    if (acquireResult == IdempotencyAcquireResult.BUSY) {
      //다른 consumer 가 처리하고 있으므로 throw 해서 retry 유도
      throw new RetryableException("이미 진행 중인 작업입니다.");
    }

    try {
      //orchestrator(k8s) 명령 실행
      executeOrchestratorCommandPort.executeProvisionInstanceCommand(new Instance(
          command.instanceId(),
          command.ownerId(),
          command.imageName(),
          DefaultNetworkPolicy.fromString(command.defaultEgressPolicy()),
          DefaultNetworkPolicy.fromString(command.defaultIngressPolicy()),
          command.cidrBlock(),
          command.cpu(),
          command.memory(),
          command.storageType(),
          command.storageSize()
      ));
      idempotency.success();
    } catch (Exception e) {
      idempotency.failed();
      throw e;
    } finally {
      //멱등성 상태 (update)
      saveIdempotencyPort.saveIdempotency(idempotency);
    }
  }
}
