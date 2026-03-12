package net.happykoo.hcp.domain.instance;

public enum InstanceStatus {
  PROVISIONING,
  RUNNING,
  RERUNNING,
  STOPPING,
  STOPPED,
  TERMINATING,
  TERMINATED,
  FAILED;
}
