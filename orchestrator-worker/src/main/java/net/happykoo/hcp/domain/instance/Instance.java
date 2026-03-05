package net.happykoo.hcp.domain.instance;

import java.util.UUID;

public record Instance(
    UUID instanceId,
    UUID ownerId,
    String imageName,
    DefaultNetworkPolicy defaultEgressPolicy,
    DefaultNetworkPolicy defaultIngressPolicy,
    String cidrBlock,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}
