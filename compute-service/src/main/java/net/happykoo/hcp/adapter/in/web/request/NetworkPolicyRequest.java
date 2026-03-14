package net.happykoo.hcp.adapter.in.web.request;

public record NetworkPolicyRequest(
    String policyName,
    String port,
    String ipCidr
) {

}
