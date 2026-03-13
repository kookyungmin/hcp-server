package net.happykoo.hcp.adapter.in.event.payload;

public record InstanceRegisterSshKeyEventPayload(
    String instanceId,
    String sshKey
) {

}
