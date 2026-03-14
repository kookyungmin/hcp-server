package net.happykoo.hcp.adapter.in.web.response;

import java.util.List;

public record GetNetworkPolicyResponse(
    String instanceId,
    List<NetworkPolicyResponse> ingressPolicies,
    List<NetworkPolicyResponse> egressPolicies
) {

}
