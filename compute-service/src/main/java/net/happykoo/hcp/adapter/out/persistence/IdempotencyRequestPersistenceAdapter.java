package net.happykoo.hcp.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaIdempotencyRequestRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyRequestEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyRequestId;
import net.happykoo.hcp.application.port.out.GetIdempotencyRequestPort;
import net.happykoo.hcp.application.port.out.SaveIdempotencyRequestPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;

@PersistenceAdapter
@RequiredArgsConstructor
public class IdempotencyRequestPersistenceAdapter implements GetIdempotencyRequestPort,
    SaveIdempotencyRequestPort {

  private final JpaIdempotencyRequestRepository jpaIdempotencyRequestRepository;

  @Override
  public Optional<IdempotencyRequest> findRequestByKey(UUID ownerId, String idempotencyKey) {
    return jpaIdempotencyRequestRepository.findById(
            new JpaIdempotencyRequestId(ownerId, idempotencyKey))
        .map(JpaIdempotencyRequestEntity::toDomain);
  }

  @Override
  public void saveIdempotencyRequest(IdempotencyRequest idempotencyRequest) {
    var entity = JpaIdempotencyRequestEntity.from(idempotencyRequest);
    jpaIdempotencyRequestRepository.save(entity);
  }
}
