package net.happykoo.hcp.adapter.out.persistence.jpa.mysql;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaOutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaMysqlOutboxEventRepository extends
    JpaRepository<JpaOutboxEventEntity, UUID> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = MysqlNativeQuery.UPDATE_PROCESSING_STATUS_QUERY, nativeQuery = true)
  void updateProcessingStatus(
      String oldStatus,
      String newStatus,
      UUID claimToken,
      int batchSize
  );

  List<JpaOutboxEventEntity> findAllByClaimTokenOrderByCreatedAtAsc(UUID claimToken);
}
