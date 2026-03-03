package net.happykoo.hcp.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaOutboxEventRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaOutboxEventEntity;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

@PersistenceAdapter
@RequiredArgsConstructor
public class OutboxEventPortPersistenceAdapter implements SaveOutboxEventPort {

  private final JpaOutboxEventRepository jpaOutboxEventRepository;

  @Override
  public void saveOutboxEvent(
      OutboxEvent outboxEvent
  ) {
    var entity = JpaOutboxEventEntity.from(outboxEvent);
    jpaOutboxEventRepository.save(entity);
  }
}
