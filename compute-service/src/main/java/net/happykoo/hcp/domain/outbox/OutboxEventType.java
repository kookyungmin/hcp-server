package net.happykoo.hcp.domain.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OutboxEventType {
  INSTANCE_PROVISIONING_EVENT("hcp.compute.instance.provisioning"),
  INSTANCE_SCALING_EVENT("hcp.compute.instance.scaling"),
  UPDATE_INSTANCE_LIFECYCLE_EVENT("hcp.compute.instance.update.lifecycle"),
  REGISTER_SSH_KEY_EVENT("hcp.compute.instance.register.sshkey"),
  UPDATE_INSTANCE_NETWORK_POLICY_EVENT("hcp.compute.instance.update.networkpolicy");

  private final String topic;
}
