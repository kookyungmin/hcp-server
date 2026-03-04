package net.happykoo.hcp.adapter.in.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.application.port.in.PublishOutboxEventUseCase;
import net.happykoo.hcp.common.annotation.EventInAdapter;
import org.springframework.scheduling.annotation.Scheduled;

@EventInAdapter
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduler {

  private final PublishOutboxEventUseCase publishOutboxEventUseCase;

  @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:2000}")
  public void poll() {
    publishOutboxEventUseCase.runOnce();
  }
}
