package net.happykoo.hcp.domain.idempotency;

public enum IdempotencyCommandType {
  INSTANCE_PROVISIONING,
  UPDATE_INSTANCE_LIFECYCLE,
  INSTANCE_SCALING,
  REGISTER_SSH_KEY,
  UPDATE_INSTANCE_NETWORK_POLICY;
}
