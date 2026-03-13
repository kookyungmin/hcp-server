package net.happykoo.hcp.adapter.in.web.request;

public record RegisterSshKey(
    String instanceId,
    String keyName,
    String sshKey
) {

}
