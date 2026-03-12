package net.happykoo.hcp.domain.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OutboxEventType {
  INSTANCE_PROVISIONING_EVENT("hcp.compute.instance.provisioning"),
  UPDATE_INSTANCE_LIFECYCLE_EVENT("hcp.compute.instance.update.lifecycle");

  private final String topic;
}
