package net.happykoo.hcp.adapter.in.web.response;

import java.util.List;
import net.happykoo.hcp.application.port.in.result.InstanceSpecResult;

public record GetSpecMetaResponse(
    String specCode,
    String specName,
    String description
) {

  public static List<GetSpecMetaResponse> from(List<InstanceSpecResult> allSpecMeta) {
    return allSpecMeta.stream()
        .map(GetSpecMetaResponse::from)
        .toList();
  }

  public static GetSpecMetaResponse from(InstanceSpecResult specMeta) {
    return new GetSpecMetaResponse(
        specMeta.specCode(),
        specMeta.specName(),
        specMeta.description()
    );
  }
}
