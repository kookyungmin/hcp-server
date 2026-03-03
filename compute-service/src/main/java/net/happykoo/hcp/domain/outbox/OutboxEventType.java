package net.happykoo.hcp.domain.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OutboxEventType {
  INSTANCE_PROVISIONING_EVENT("hcp.compute.instance.provisioning"),
  ;
  private final String topic;
}
