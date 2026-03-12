package net.happykoo.hcp.domain.instance;

public enum InstanceStatus {
  PROVISIONING,
  RUNNING,
  RESTARTING,
  STOPPING,
  STOPPED,
  TERMINATING,
  TERMINATED,
  FAILED;
}
