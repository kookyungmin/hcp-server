package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;

public record GetNetworkPolicyCommand(
    UUID instanceId,
    UUID requesterId
) {

}
