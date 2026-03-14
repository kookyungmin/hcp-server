package net.happykoo.hcp.adapter.in.web.response;

public record NetworkPolicyResponse(
    String policyName,
    String port,
    String ipCidr
) {

}
