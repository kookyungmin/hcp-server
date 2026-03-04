package net.happykoo.hcp.application.port.out;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

public interface PublishOutboxEventPort {

  void publishOutboxEvent(OutboxEvent outboxEvent)
      throws ExecutionException, InterruptedException, TimeoutException;
}
