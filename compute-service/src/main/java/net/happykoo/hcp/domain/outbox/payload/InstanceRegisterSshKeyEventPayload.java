package net.happykoo.hcp.domain.outbox.payload;

public record InstanceRegisterSshKeyEventPayload(
    String instanceId,
    String sshKey
) {

}
