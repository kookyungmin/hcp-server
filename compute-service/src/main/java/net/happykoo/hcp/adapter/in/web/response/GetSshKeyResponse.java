package net.happykoo.hcp.adapter.in.web.response;

import java.util.UUID;

public record GetSshKeyResponse(
    UUID instanceId,
    String keyName,
    String sshKey
) {

}
