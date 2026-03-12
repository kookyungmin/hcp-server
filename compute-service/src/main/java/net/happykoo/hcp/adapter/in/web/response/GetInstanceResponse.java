package net.happykoo.hcp.adapter.in.web.response;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.ServerInstance;

public record GetInstanceResponse(
    UUID instanceId,
    UUID ownerId,
    String name,
    Set<String> tags,
    String imageCode,
    String specCode,
    String vpcCode,
    String storageType,
    int storageSize,
    InstanceStatus status,
    String publicIp,
    String privateIp
) {

  public static GetInstanceResponse from(ServerInstance instance) {
    return new GetInstanceResponse(
        instance.getInstanceId(),
        instance.getOwnerId(),
        instance.getName(),
        instance.getTags(),
        instance.getImageCode(),
        instance.getSpecCode(),
        instance.getVpcCode(),
        instance.getStorageType(),
        instance.getStorageSize(),
        instance.getStatus(),
        instance.getPublicIp(),
        instance.getPrivateIp()
    );
  }
}
