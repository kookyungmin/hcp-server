package net.happykoo.hcp.application.port.in.command;

import java.util.List;
import java.util.UUID;

public record OpenTerminalSessionCommand(
    String sessionId,
    UUID instanceId,
    UUID userId,
    List<String> roles
) {

}
