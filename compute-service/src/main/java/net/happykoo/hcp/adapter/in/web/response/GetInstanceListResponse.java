package net.happykoo.hcp.adapter.in.web.response;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.ServerInstance;

public record GetInstanceListResponse(
    UUID instanceId,
    UUID ownerId,
    String name,
    Set<String> tags,
    String osName,
    String osVersion,
    String vpcName,
    String cpu,
    String memory,
    int storageSize,
    InstanceStatus status,
    String publicIp,
    String privateIp,
    String message
) {

  public static GetInstanceListResponse from(ServerInstance instance) {
    return new GetInstanceListResponse(
        instance.getInstanceId(),
        instance.getOwnerId(),
        instance.getName(),
        instance.getTags(),
        instance.getOsName(),
        instance.getOsVersion(),
        instance.getVpcName(),
        instance.getCpu(),
        instance.getMemory(),
        instance.getStorageSize(),
        instance.getStatus(),
        instance.getPublicIp(),
        instance.getPrivateIp(),
        instance.getMessage()
    );
  }
}
