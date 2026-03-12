package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record ScaleInstanceCommand(
    UUID eventId,
    UUID instanceId,
    UUID ownerId,
    String cpu,
    String memory,
    String storageType,
    int storageSize
) {

}
