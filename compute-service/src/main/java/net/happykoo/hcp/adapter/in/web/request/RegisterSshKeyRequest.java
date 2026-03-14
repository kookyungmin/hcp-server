package net.happykoo.hcp.adapter.in.web.request;

public record RegisterSshKeyRequest(
    String instanceId,
    String keyName,
    String sshKey
) {

}
