package net.happykoo.hcp.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.application.port.out.GetOutboxEventPort;
import net.happykoo.hcp.application.port.out.PublishOutboxEventPort;
import net.happykoo.hcp.application.port.out.SaveOutboxEventPort;
import net.happykoo.hcp.domain.outbox.OutboxEvent;
import net.happykoo.hcp.domain.outbox.OutboxEventType;
import net.happykoo.hcp.domain.outbox.OutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceTest {

  @Mock
  private GetOutboxEventPort getOutboxEventPort;
  @Mock
  private PublishOutboxEventPort publishOutboxEventPort;
  @Mock
  private SaveOutboxEventPort saveOutboxEventPort;

  @InjectMocks
  private OutboxEventService outboxEventService;

  @Test
  @DisplayName("runOnce() :: 발행 성공 시 이벤트 상태를 SUCCESS 로 저장")
  void runOnceTest1() throws Exception {
    OutboxEvent event = new OutboxEvent(
        UUID.randomUUID(),
        OutboxEventType.INSTANCE_PROVISIONING_EVENT,
        "{}",
        "req-1",
        OutboxStatus.PENDING,
        0
    );
    when(getOutboxEventPort.claimPendingOutboxEvent()).thenReturn(List.of(event));

    outboxEventService.runOnce();

    assertEquals(OutboxStatus.SUCCESS, event.getStatus());
    verify(saveOutboxEventPort).saveOutboxEvent(event);
  }

  @Test
  @DisplayName("runOnce() :: 발행 실패 시 이벤트 상태를 FAILED 로 저장")
  void runOnceTest2() throws Exception {
    OutboxEvent event = new OutboxEvent(
        UUID.randomUUID(),
        OutboxEventType.INSTANCE_PROVISIONING_EVENT,
        "{}",
        "req-1",
        OutboxStatus.PENDING,
        0
    );
    when(getOutboxEventPort.claimPendingOutboxEvent()).thenReturn(List.of(event));
    doThrow(new RuntimeException("publish failed"))
        .when(publishOutboxEventPort)
        .publishOutboxEvent(any(OutboxEvent.class));

    outboxEventService.runOnce();

    assertEquals(OutboxStatus.FAILED, event.getStatus());
    verify(saveOutboxEventPort).saveOutboxEvent(event);
  }
}
