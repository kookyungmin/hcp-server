package net.happykoo.hcp.adapter.in.event.payload;

import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceNetworkPolicy;
import net.happykoo.hcp.domain.instance.NetworkPolicyType;

public record InstanceNetworkPolicyEventPayload(
    String instanceId,
    String name,
    String type,
    String port,
    String ipCidr
) {

  public InstanceNetworkPolicy toDomain() {
    return new InstanceNetworkPolicy(
        UUID.fromString(instanceId),
        NetworkPolicyType.valueOf(type),
        name,
        ipCidr,
        port
    );
  }
}
