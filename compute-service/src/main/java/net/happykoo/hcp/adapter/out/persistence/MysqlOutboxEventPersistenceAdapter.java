package net.happykoo.hcp.adapter.out.persistence;

import static net.happykoo.hcp.domain.outbox.OutboxStatus.PENDING;
import static net.happykoo.hcp.domain.outbox.OutboxStatus.PROCESSING;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaOutboxEventEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.mysql.JpaMysqlOutboxEventRepository;
import net.happykoo.hcp.application.port.out.GetOutboxEventPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.infrastructure.properties.OutboxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.Transactional;

@PersistenceAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(OutboxProperties.class)
public class MysqlOutboxEventPersistenceAdapter implements GetOutboxEventPort {

  private final JpaMysqlOutboxEventRepository jpaMysqlOutboxEventRepository;
  private final OutboxProperties outboxProperties;

  @Override
  @Transactional
  public List<OutboxEvent> claimPendingOutboxEvent() {
    UUID claimToken = UUID.randomUUID();
    jpaMysqlOutboxEventRepository.updateProcessingStatus(
        PENDING.name(),
        PROCESSING.name(),
        claimToken,
        outboxProperties.batchSize());
    return jpaMysqlOutboxEventRepository.findAllByClaimTokenOrderByCreatedAtAsc(claimToken)
        .stream()
        .map(JpaOutboxEventEntity::toDomain)
        .toList();
  }
}
