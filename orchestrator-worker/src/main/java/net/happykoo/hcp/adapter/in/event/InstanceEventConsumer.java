package net.happykoo.hcp.adapter.in.event;

import com.google.gson.Gson;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.event.payload.InstanceProvisioningEvent;
import net.happykoo.hcp.adapter.in.event.payload.InstanceRegisterSshKeyEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceScalingEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceUpdateLifecycleEventPayload;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.RegisterInstanceSshKeyUseCase;
import net.happykoo.hcp.application.port.in.ScaleInstanceUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceLifecycleUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.ScaleInstanceCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
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
  private final UpdateInstanceLifecycleUseCase updateInstanceLifecycleUseCase;
  private final ScaleInstanceUseCase scaleInstanceUseCase;
  private final RegisterInstanceSshKeyUseCase registerInstanceSshKeyUseCase;

  @KafkaListener(topics = KafkaTopics.INSTANCE_PROVISIONING_TOPIC)
  public void onInstanceProvisioning(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    if (StringUtils.isBlank(record.key())) {
      ack.acknowledge();
      return;
    }
    var event = new Gson().fromJson(record.value(),
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

  @KafkaListener(topics = KafkaTopics.INSTANCE_UPDATE_LIFECYCLE_TOPIC)
  public void onUpdateInstanceLifecycle(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    if (StringUtils.isBlank(record.key())) {
      ack.acknowledge();
      return;
    }
    var event = new Gson().fromJson(record.value(), InstanceUpdateLifecycleEventPayload.class);
    var command = new UpdateInstanceLifecycleCommand(
        UUID.fromString(record.key()),
        UUID.fromString(event.instanceId()),
        UUID.fromString(event.ownerId())
    );
    switch (event.instanceStatus()) {
      case "STOPPING" -> updateInstanceLifecycleUseCase.stopInstance(command);
      case "RESTARTING" -> updateInstanceLifecycleUseCase.restartInstance(command);
      case "TERMINATING" -> updateInstanceLifecycleUseCase.terminateInstance(command);
    }

    ack.acknowledge();
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_SCALING_TOPIC)
  public void onInstanceScaling(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    if (StringUtils.isBlank(record.key())) {
      ack.acknowledge();
      return;
    }
    var event = new Gson().fromJson(record.value(), InstanceScalingEventPayload.class);

    scaleInstanceUseCase.scaleInstance(
        new ScaleInstanceCommand(
            UUID.fromString(record.key()),
            UUID.fromString(event.instanceId()),
            UUID.fromString(event.ownerId()),
            event.cpu(),
            event.memory(),
            event.storageType(),
            event.storageSize()
        )
    );

    ack.acknowledge();
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_REGISTER_SSH_KEY_TOPIC)
  public void onRegisterInstanceSshKey(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    if (StringUtils.isBlank(record.key())) {
      ack.acknowledge();
      return;
    }
    var event = new Gson().fromJson(record.value(), InstanceRegisterSshKeyEventPayload.class);

    registerInstanceSshKeyUseCase.registerInstanceSshKey(
        new RegisterInstanceSshKeyCommand(
            UUID.fromString(event.instanceId()),
            event.sshKey()
        )
    );
    ack.acknowledge();
  }
}
