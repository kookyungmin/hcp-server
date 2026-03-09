package net.happykoo.hcp.application.port.out.data;

import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceStatus;

public record UpdateInstanceStatusData(
    UUID instanceId,
    InstanceStatus status,
    String failureReason,
    String publicIp,
    String privateIp
) {

}
