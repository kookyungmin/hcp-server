package net.happykoo.hcp.adapter.in.event;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.event.payload.InstanceNetworkPolicyEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceProvisioningEvent;
import net.happykoo.hcp.adapter.in.event.payload.InstanceRegisterSshKeyEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceScalingEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceUpdateLifecycleEventPayload;
import net.happykoo.hcp.adapter.in.event.payload.InstanceUpdateNetworkPolicyEventPayload;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.RegisterInstanceSshKeyUseCase;
import net.happykoo.hcp.application.port.in.ScaleInstanceUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceLifecycleUseCase;
import net.happykoo.hcp.application.port.in.UpdateNetworkPolicyUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.ScaleInstanceCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
import net.happykoo.hcp.application.port.in.command.UpdateNetworkPolicyCommand;
import net.happykoo.hcp.common.annotation.EventInAdapter;
import net.happykoo.hcp.common.web.logging.RequestIdMdcSupport;
import net.happykoo.hcp.common.web.security.SecurityHeaderNames;
import net.happykoo.hcp.infrastructure.kafka.topic.KafkaTopics;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@EventInAdapter
@RequiredArgsConstructor
@Slf4j
public class InstanceEventConsumer {

  private final ProvisionInstanceUseCase provisionInstanceUseCase;
  private final UpdateInstanceLifecycleUseCase updateInstanceLifecycleUseCase;
  private final ScaleInstanceUseCase scaleInstanceUseCase;
  private final RegisterInstanceSshKeyUseCase registerInstanceSshKeyUseCase;
  private final UpdateNetworkPolicyUseCase updateNetworkPolicyUseCase;

  @KafkaListener(topics = KafkaTopics.INSTANCE_PROVISIONING_TOPIC)
  public void onInstanceProvisioning(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    var previousRequestId = RequestIdMdcSupport.bind(resolveRequestId(record));
    try {
      if (StringUtils.isBlank(record.key())) {
        log.warn("인스턴스 생성 이벤트를 무시합니다. eventId가 비어 있습니다.");
        ack.acknowledge();
        return;
      }
      var event = new Gson().fromJson(record.value(),
          InstanceProvisioningEvent.class);
      log.info("인스턴스 생성 이벤트를 수신했습니다. eventId={}, instanceId={}", record.key(),
          event.instanceId());
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
    } finally {
      RequestIdMdcSupport.restore(previousRequestId);
    }
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_UPDATE_LIFECYCLE_TOPIC)
  public void onUpdateInstanceLifecycle(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    var previousRequestId = RequestIdMdcSupport.bind(resolveRequestId(record));
    try {
      if (StringUtils.isBlank(record.key())) {
        log.warn("인스턴스 라이프사이클 이벤트를 무시합니다. eventId가 비어 있습니다.");
        ack.acknowledge();
        return;
      }
      var event = new Gson().fromJson(record.value(), InstanceUpdateLifecycleEventPayload.class);
      log.info("인스턴스 라이프사이클 이벤트를 수신했습니다. eventId={}, instanceId={}, status={}",
          record.key(), event.instanceId(), event.instanceStatus());
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
    } finally {
      RequestIdMdcSupport.restore(previousRequestId);
    }
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_SCALING_TOPIC)
  public void onInstanceScaling(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    var previousRequestId = RequestIdMdcSupport.bind(resolveRequestId(record));
    try {
      if (StringUtils.isBlank(record.key())) {
        log.warn("인스턴스 스케일링 이벤트를 무시합니다. eventId가 비어 있습니다.");
        ack.acknowledge();
        return;
      }
      var event = new Gson().fromJson(record.value(), InstanceScalingEventPayload.class);
      log.info("인스턴스 스케일링 이벤트를 수신했습니다. eventId={}, instanceId={}, cpu={}, memory={}",
          record.key(), event.instanceId(), event.cpu(), event.memory());

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
    } finally {
      RequestIdMdcSupport.restore(previousRequestId);
    }
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_REGISTER_SSH_KEY_TOPIC)
  public void onRegisterInstanceSshKey(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    var previousRequestId = RequestIdMdcSupport.bind(resolveRequestId(record));
    try {
      if (StringUtils.isBlank(record.key())) {
        log.warn("SSH 키 등록 이벤트를 무시합니다. eventId가 비어 있습니다.");
        ack.acknowledge();
        return;
      }
      var event = new Gson().fromJson(record.value(), InstanceRegisterSshKeyEventPayload.class);
      log.info("SSH 키 등록 이벤트를 수신했습니다. instanceId={}", event.instanceId());

      registerInstanceSshKeyUseCase.registerInstanceSshKey(
          new RegisterInstanceSshKeyCommand(
              UUID.fromString(event.instanceId()),
              event.sshKey()
          )
      );
      ack.acknowledge();
    } finally {
      RequestIdMdcSupport.restore(previousRequestId);
    }
  }

  @KafkaListener(topics = KafkaTopics.INSTANCE_UPDATE_NETWORK_POLICY_TOPIC)
  public void onUpdateNetworkPolicy(
      ConsumerRecord<String, String> record,
      Acknowledgment ack
  ) {
    var previousRequestId = RequestIdMdcSupport.bind(resolveRequestId(record));
    try {
      if (StringUtils.isBlank(record.key())) {
        log.warn("네트워크 정책 이벤트를 무시합니다. eventId가 비어 있습니다.");
        ack.acknowledge();
        return;
      }
      var event = new Gson().fromJson(record.value(), InstanceUpdateNetworkPolicyEventPayload.class);
      log.info("네트워크 정책 이벤트를 수신했습니다. eventId={}, instanceId={}, policyCount={}",
          record.key(), event.instanceId(), event.networkPolicies().size());
      var networkPolices = event.networkPolicies()
          .stream()
          .map(InstanceNetworkPolicyEventPayload::toDomain)
          .toList();

      updateNetworkPolicyUseCase.updateNetworkPolicy(new UpdateNetworkPolicyCommand(
          UUID.fromString(record.key()),
          UUID.fromString(event.instanceId()),
          networkPolices
      ));

      ack.acknowledge();
    } finally {
      RequestIdMdcSupport.restore(previousRequestId);
    }
  }

  private String resolveRequestId(ConsumerRecord<String, String> record) {
    var header = record.headers().lastHeader(SecurityHeaderNames.X_REQUEST_ID);
    if (header == null || header.value() == null) {
      return null;
    }
    return new String(header.value(), StandardCharsets.UTF_8);
  }
}
