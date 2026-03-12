package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record UpdateInstanceLifecycleCommand(
    UUID eventId,
    UUID instanceId,
    UUID ownerId
) {

}
