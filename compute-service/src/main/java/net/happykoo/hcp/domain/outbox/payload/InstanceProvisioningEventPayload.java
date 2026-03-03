package net.happykoo.hcp.domain.outbox.payload;

public record InstanceProvisioningEventPayload(
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
