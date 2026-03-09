package net.happykoo.hcp.adapter.in.event;

import com.google.gson.Gson;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.event.payload.InstanceProvisioningEvent;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.common.annotation.EventInAdapter;
import net.happykoo.hcp.infrastructure.kafka.topic.KafkaTopics;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@EventInAdapter
@RequiredArgsConstructor
public class InstanceEventConsumer {

  private final ProvisionInstanceUseCase provisionInstanceUseCase;

  @KafkaListener(topics = KafkaTopics.INSTANCE_PROVISIONING_TOPIC)
  public void onInstanceProvisioning(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    if (StringUtils.isBlank(record.key())) {
      ack.acknowledge();
      return;
    }
    InstanceProvisioningEvent event = new Gson().fromJson(record.value(),
        InstanceProvisioningEvent.class);
    provisionInstanceUseCase.provisionInstance(new ProvisionInstanceCommand(
        UUID.fromString(record.key()),
        UUID.fromString(event.instanceId()),
        UUID.fromString(event.ownerId()),
        event.imageName(),
        event.defaultEgressPolicy(),
        event.defaultIngressPolicy(),
        event.cidrBlock(),
        event.cpu(),
        event.memory(),
        event.storageType(),
        event.storageSize()
    ));
    ack.acknowledge();
  }
}
