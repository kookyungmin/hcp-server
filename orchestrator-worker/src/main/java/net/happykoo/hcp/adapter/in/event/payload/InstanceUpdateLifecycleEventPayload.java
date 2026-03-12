package net.happykoo.hcp.adapter.in.event.payload;

public record InstanceUpdateLifecycleEventPayload(
    String instanceId,
    String ownerId,
    String instanceStatus
) {

}
