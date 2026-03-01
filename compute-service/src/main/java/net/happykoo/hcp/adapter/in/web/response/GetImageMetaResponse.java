package net.happykoo.hcp.adapter.in.web.response;

import java.util.List;
import net.happykoo.hcp.application.port.in.result.InstanceImageResult;

public record GetImageMetaResponse(
    String imageCode,
    String osName,
    String osVersion,
    String description
) {

  public static List<GetImageMetaResponse> from(List<InstanceImageResult> allImageMeta) {
    return allImageMeta.stream()
        .map(GetImageMetaResponse::from)
        .toList();
  }

  public static GetImageMetaResponse from(InstanceImageResult imageMeta) {
    return new GetImageMetaResponse(
        imageMeta.imageCode(),
        imageMeta.osName(),
        imageMeta.osVersion(),
        imageMeta.description()
    );
  }
}
