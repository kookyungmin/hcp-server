package net.happykoo.hcp.application.port.in.command;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.domain.network.NetworkPolicy;

public record UpdateNetworkPolicyCommand(
    UUID instanceId,
    UUID requesterId,
    String idempotencyKey,
    List<NetworkPolicy> networkPolicies
) {

}
