package net.happykoo.hcp.domain.outbox.payload;

public record InstanceScalingEventPayload(
    String instanceId,
    String ownerId,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}
