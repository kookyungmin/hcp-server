package net.happykoo.hcp.domain.outbox.payload;

import net.happykoo.hcp.domain.instance.InstanceStatus;

public record InstanceUpdateLifecycleEventPayload(
    String instanceId,
    String ownerId,
    InstanceStatus instanceStatus
) {

}
