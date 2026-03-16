package net.happykoo.hcp.application.port.in.command;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceNetworkPolicy;

public record UpdateNetworkPolicyCommand(
    UUID eventId,
    UUID instanceId,
    List<InstanceNetworkPolicy> networkPolicies
) {

}
