package net.happykoo.hcp.adapter.in.event.payload;

public record InstanceProvisioningEvent(
    String instanceId,
    String ownerId,
    String imageName,
    String defaultEgressPolicy,
    String defaultIngressPolicy,
    String cidrBlock,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}