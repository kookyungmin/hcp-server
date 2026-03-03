package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.outbox.OutboxEvent;

public interface SaveOutboxEventPort {

  void saveOutboxEvent(OutboxEvent outboxEvent);

}
