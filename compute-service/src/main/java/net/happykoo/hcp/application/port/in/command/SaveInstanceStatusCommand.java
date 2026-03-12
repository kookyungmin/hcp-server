package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceStatus;

public record SaveInstanceStatusCommand(
    UUID instanceId,
    InstanceStatus status,
    String failureReason,
    String publicIp,
    String privateIp
) {

  public SaveInstanceStatusCommand(
      UUID instanceId,
      InstanceStatus status
  ) {
    this(instanceId, status, null, null, null);
  }

}
