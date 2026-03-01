package net.happykoo.hcp.domain.instance;

public enum ServerStatus {
  PROVISIONING,
  RUNNING,
  STOPPING,
  STOPPED,
  TERMINATING,
  TERMINATED,
  FAILED;
}
