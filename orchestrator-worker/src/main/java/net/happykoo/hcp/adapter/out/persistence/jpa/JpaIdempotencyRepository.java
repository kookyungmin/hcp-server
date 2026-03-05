package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.time.Instant;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyEntity;
import net.happykoo.hcp.domain.idempotency.IdempotencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaIdempotencyRepository extends JpaRepository<JpaIdempotencyEntity, UUID> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        UPDATE JpaIdempotencyEntity e
           SET e.status = :status,
               e.updatedAt = :now
         WHERE e.idempotencyKey = :idempotencyKey
           AND e.status <> :doneStatus
           AND e.expiredAt < :now
      """)
  int acquireIfExpired(
      @Param("idempotencyKey") UUID idempotencyKey,
      @Param("status") IdempotencyStatus status,
      @Param("doneStatus") IdempotencyStatus doneStatus,
      @Param("now") Instant now
  );

}
