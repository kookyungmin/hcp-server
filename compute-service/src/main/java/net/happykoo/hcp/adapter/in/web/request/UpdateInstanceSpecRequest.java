package net.happykoo.hcp.adapter.in.web.request;

public record UpdateInstanceSpecRequest(
    String instanceId,
    String specCode,
    String storageType,
    int storageSize
) {

}
