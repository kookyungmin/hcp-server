package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record FindInstanceSshKeyCommand(
    UUID instanceId,
    UUID requesterId
) {

}
