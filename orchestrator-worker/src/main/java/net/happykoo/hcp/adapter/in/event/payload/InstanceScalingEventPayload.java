package net.happykoo.hcp.adapter.in.event.payload;

public record InstanceScalingEventPayload(
    String instanceId,
    String ownerId,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}
