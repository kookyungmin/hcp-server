package net.happykoo.hcp.application.port.in.result;

public record InstanceImageResult(
    String imageCode,
    String osName,
    String osVersion,
    String description
) {

}
