package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record RegisterInstanceSshKeyCommand(
    UUID instanceId,
    String sshKey
) {

}
