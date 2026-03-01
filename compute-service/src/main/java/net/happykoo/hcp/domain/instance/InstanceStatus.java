package net.happykoo.hcp.domain.instance;

public enum InstanceStatus {
  PROVISIONING,
  RUNNING,
  STOPPING,
  STOPPED,
  TERMINATING,
  TERMINATED,
  FAILED;
}
