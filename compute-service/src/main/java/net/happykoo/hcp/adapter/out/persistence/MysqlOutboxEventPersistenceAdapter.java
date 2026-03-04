package net.happykoo.hcp.adapter.out.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.GetOutboxEventPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

@PersistenceAdapter
@RequiredArgsConstructor
public class MysqlOutboxEventPersistenceAdapter implements GetOutboxEventPort {

  @Override
  public List<OutboxEvent> findAllPendingOutboxEvent() {
    return List.of();
  }
}
