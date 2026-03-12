package net.happykoo.hcp.adapter.in.web.request;

public record UpdateInstanceTagRequest(
    String instanceId,
    String tags
) {

}
