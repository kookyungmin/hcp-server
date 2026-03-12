package net.happykoo.hcp.adapter.in.event;

import static net.happykoo.hcp.domain.instance.InstanceStatus.FAILED;
import static net.happykoo.hcp.domain.instance.InstanceStatus.PROVISIONING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.RUNNING;
import static net.happykoo.hcp.domain.instance.InstanceStatus.STOPPED;
import static net.happykoo.hcp.domain.instance.InstanceStatus.TERMINATED;

import com.google.gson.Gson;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.event.payload.EventStatus;
import net.happykoo.hcp.adapter.in.event.payload.InstanceStatusEvent;
import net.happykoo.hcp.application.port.in.SaveInstanceStatusUseCase;
import net.happykoo.hcp.application.port.in.command.SaveInstanceStatusCommand;
import net.happykoo.hcp.common.annotation.EventInAdapter;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.outbox.payload.InstanceProvisioningEventPayload;
import net.happykoo.hcp.infrastructure.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@EventInAdapter
@RequiredArgsConstructor
@Slf4j
public class InstanceStatusEventConsumer {

  private final SaveInstanceStatusUseCase saveInstanceStatusUseCase;


  @KafkaListener(topics = KafkaTopics.INSTANCE_STATUS_TOPIC)
  public void onInstanceStatusUpdateEvent(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    InstanceStatusEvent event = new Gson().fromJson(record.value(),
        InstanceStatusEvent.class);

    saveInstanceStatusUseCase.saveInstanceStatus(
        new SaveInstanceStatusCommand(
            UUID.fromString(event.instanceId()),
            resolveStatus(event.status()),
            event.message(),
            event.publicIp(),
            event.privateIp()
        )
    );

    ack.acknowledge();
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_PROVISIONING_DLT_TOPIC)
  public void onInstanceProvisioningDltEvent(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    InstanceProvisioningEventPayload event = new Gson().fromJson(record.value(),
        InstanceProvisioningEventPayload.class);

    saveInstanceStatusUseCase.saveInstanceStatus(
        new SaveInstanceStatusCommand(
            UUID.fromString(event.instanceId()),
            FAILED,
            "Kafka Consumer 에서 에러가 발생하여 이벤트를 처리하지 못하였습니다.",
            null,
            null
        )
    );

    ack.acknowledge();
  }

  private InstanceStatus resolveStatus(EventStatus status) {
    return switch (status) {
      case SUCCESS -> RUNNING;
      case PROCESSING -> PROVISIONING;
      case STOPPED -> STOPPED;
      case DELETED -> TERMINATED;
      case FAILED -> FAILED;
    };
  }
}
