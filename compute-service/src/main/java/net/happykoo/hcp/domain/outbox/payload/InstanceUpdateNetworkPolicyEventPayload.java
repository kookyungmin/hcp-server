package net.happykoo.hcp.domain.outbox.payload;

import java.util.List;

public record InstanceUpdateNetworkPolicyEventPayload(
    String instanceId,
    List<InstanceNetworkPolicyEventPayload> networkPolicies
) {

}
