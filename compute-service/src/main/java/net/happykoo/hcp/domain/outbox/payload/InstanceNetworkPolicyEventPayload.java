package net.happykoo.hcp.domain.outbox.payload;

public record InstanceNetworkPolicyEventPayload(
    String instanceId,
    String type,
    String port,
    String ipCidr
) {

}
