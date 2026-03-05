package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record ProvisionInstanceCommand(
    UUID eventId,
    UUID instanceId,
    UUID ownerId,
    String imageName,
    String defaultEgressPolicy,
    String defaultIngressPolicy,
    String cidrBlock,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}