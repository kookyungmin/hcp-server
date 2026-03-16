package net.happykoo.hcp.adapter.in.event.payload;

import java.util.List;

public record InstanceUpdateNetworkPolicyEventPayload(
    String instanceId,
    List<InstanceNetworkPolicyEventPayload> networkPolicies
) {

}
