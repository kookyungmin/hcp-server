package net.happykoo.hcp.adapter.out.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.PublishOutboxEventPort;
import net.happykoo.hcp.common.annotation.EventOutAdapter;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

@EventOutAdapter
@RequiredArgsConstructor
public class OutboxEventAdapter implements PublishOutboxEventPort {

  @Override
  public void publishOutboxEvent(OutboxEvent outboxEvent)
      throws ExecutionException, InterruptedException, TimeoutException {
    CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
      throw new RuntimeException("test");
    });

    cf.get(5, TimeUnit.SECONDS);
  }
}
