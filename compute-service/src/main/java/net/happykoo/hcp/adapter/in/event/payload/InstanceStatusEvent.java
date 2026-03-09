package net.happykoo.hcp.adapter.in.event.payload;


public record InstanceStatusEvent(
    String instanceId,
    EventStatus status,
    String message,
    String publicIp,
    String privateIp
) {

}
