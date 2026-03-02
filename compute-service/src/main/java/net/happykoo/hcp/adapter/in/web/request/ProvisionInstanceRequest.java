package net.happykoo.hcp.adapter.in.web.request;

public record ProvisionInstanceRequest(
    String name,
    String tags,
    String imageCode,
    String vpcCode,
    String specCode,
    String storageType,
    int storageSize
) {

}
