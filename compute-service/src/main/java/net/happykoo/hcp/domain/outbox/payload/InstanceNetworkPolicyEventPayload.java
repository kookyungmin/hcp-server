package net.happykoo.hcp.domain.outbox.payload;

public record InstanceNetworkPolicyEventPayload(
    String instanceId,
    String name,
    String type,
    String port,
    String ipCidr
) {

}
