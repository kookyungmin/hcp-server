package net.happykoo.hcp.adapter.out.persistence;

import static net.happykoo.hcp.domain.idempotency.IdempotencyStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaIdempotencyAcquireRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaIdempotencyInsertRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaIdempotencyRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyEntity;
import net.happykoo.hcp.application.port.out.SaveIdempotencyPort;
import net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.idempotency.Idempotency;
import net.happykoo.hcp.domain.idempotency.IdempotencyStatus;
import org.springframework.transaction.annotation.Transactional;

@PersistenceAdapter
@RequiredArgsConstructor
public class IdempotencyPersistenceAdapter implements SaveIdempotencyPort {

  private final JpaIdempotencyRepository jpaIdempotencyRepository;
  private final JpaIdempotencyAcquireRepository jpaIdempotencyAcquireRepository;
  private final JpaIdempotencyInsertRepository jpaIdempotencyInsertRepository;

  @Override
  public IdempotencyAcquireResult tryAcquireIdempotency(Idempotency idempotency) {
    int updated = jpaIdempotencyAcquireRepository.acquireIfExpired(idempotency);
    if (updated == 1) {
      //TTL 지난 Idempotency update 성공 (작업 선점)
      return IdempotencyAcquireResult.ACQUIRED;
    }

    try {
      //지난 요청이 없어서 Idempotency insert 성공 (작업 선점)
      jpaIdempotencyInsertRepository.tryInsert(JpaIdempotencyEntity.from(idempotency));
      return IdempotencyAcquireResult.ACQUIRED;
    } catch (Exception e) {
      var status = jpaIdempotencyRepository.findById(idempotency.getIdempotencyKey())
          .map(JpaIdempotencyEntity::getStatus)
          .orElse(IdempotencyStatus.FAILED);
      if (SUCCESS == status) {
        //지난 요청이 완료된 경우
        return IdempotencyAcquireResult.ALREADY_DONE;
      }
      //지난 요청이 아직 작업중이고 TTL 이 안 지난 경우
      return IdempotencyAcquireResult.BUSY;
    }
  }

  @Override
  @Transactional
  public void saveIdempotency(Idempotency idempotency) {
    var entity = JpaIdempotencyEntity.from(idempotency);
    jpaIdempotencyRepository.save(entity);

  }
}
