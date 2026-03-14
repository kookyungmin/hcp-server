package net.happykoo.hcp.adapter.in.web.request;

import java.util.List;

public record UpdateNetworkPolicyRequest(
    String instanceId,
    List<NetworkPolicyRequest> ingressPolicies,
    List<NetworkPolicyRequest> egressPolicies
) {

}
