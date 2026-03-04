package net.happykoo.hcp.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.PublishOutboxEventUseCase;
import net.happykoo.hcp.application.port.out.GetOutboxEventPort;
import net.happykoo.hcp.application.port.out.PublishOutboxEventPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.outbox.OutboxEvent;

@UseCase
@RequiredArgsConstructor
public class OutboxEventService implements PublishOutboxEventUseCase {

  private final GetOutboxEventPort getOutboxEventPort;
  private final PublishOutboxEventPort publishOutboxEventPort;
  private final SaveOutboxEventPort saveOutboxEventPort;

  @Override
  public void runOnce() {
    //Pending event 조회 (분산 락 보장 되어야 함)
    List<OutboxEvent> events = getOutboxEventPort.findAllPendingOutboxEvent();

    for (OutboxEvent event : events) {
      try {
        publishOutboxEventPort.publishOutboxEvent(event);
        //전송 성공
        event.success();
      } catch (Exception e) {
        //전송 실패
        event.failed();
      }
      saveOutboxEventPort.saveOutboxEvent(event);
    }
  }
}
