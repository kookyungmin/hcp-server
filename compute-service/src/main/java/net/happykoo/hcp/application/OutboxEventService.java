package net.happykoo.hcp.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.application.port.in.PublishOutboxEventUseCase;
import net.happykoo.hcp.application.port.out.GetOutboxEventPort;
import net.happykoo.hcp.application.port.out.PublishOutboxEventPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class OutboxEventService implements PublishOutboxEventUseCase {

  private final GetOutboxEventPort getOutboxEventPort;
  private final PublishOutboxEventPort publishOutboxEventPort;
  private final SaveOutboxEventPort saveOutboxEventPort;

  @Override
  public void runOnce() {
    //Pending event 조회 (분산 락 보장 되어야 함)
    List<OutboxEvent> events = getOutboxEventPort.claimPendingOutboxEvent();
    if (!events.isEmpty()) {
      log.info("아웃박스 이벤트 발행을 시작합니다. count={}", events.size());
    }

    for (OutboxEvent event : events) {
      try {
        publishOutboxEventPort.publishOutboxEvent(event);
        //전송 성공
        event.success();
        log.info("아웃박스 이벤트 발행에 성공했습니다. eventId={}, topic={}",
            event.getEventId(), event.getEventType().getTopic());
      } catch (Exception e) {
        //전송 실패
        event.failed();
        log.error("아웃박스 이벤트 발행에 실패했습니다. eventId={}, topic={}",
            event.getEventId(), event.getEventType().getTopic(), e);
      }
      saveOutboxEventPort.saveOutboxEvent(event);
    }
  }
}
