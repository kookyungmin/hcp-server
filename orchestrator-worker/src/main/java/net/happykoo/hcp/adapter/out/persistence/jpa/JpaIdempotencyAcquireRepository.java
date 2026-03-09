package net.happykoo.hcp.adapter.out.persistence.jpa;

import static net.happykoo.hcp.domain.idempotency.IdempotencyStatus.SUCCESS;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.domain.idempotency.Idempotency;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JpaIdempotencyAcquireRepository {

  private final JpaIdempotencyRepository jpaIdempotencyRepository;

  @Transactional
  public int acquireIfExpired(Idempotency idempotency) {
    return jpaIdempotencyRepository.acquireIfExpired(
        idempotency.getIdempotencyKey(),
        idempotency.getStatus(),
        SUCCESS,
        Instant.now()
    );
  }
}
