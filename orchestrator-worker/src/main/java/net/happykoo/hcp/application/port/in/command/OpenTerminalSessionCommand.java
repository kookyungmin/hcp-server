package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record OpenTerminalSessionCommand(
    String sessionId,
    UUID instanceId
) {

}
