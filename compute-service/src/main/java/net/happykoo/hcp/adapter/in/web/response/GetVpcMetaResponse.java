package net.happykoo.hcp.adapter.in.web.response;

import java.util.List;
import net.happykoo.hcp.application.port.in.result.NetworkVpcResult;
import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;

public record GetVpcMetaResponse(
    String vpcCode,
    String vpcName,
    String description,
    String cidrBlock,
    DefaultNetworkPolicy defaultEgressPolicy,
    DefaultNetworkPolicy defaultIngressPolicy
) {

  public static List<GetVpcMetaResponse> from(List<NetworkVpcResult> allVpcMeta) {
    return allVpcMeta.stream()
        .map(GetVpcMetaResponse::from)
        .toList();
  }

  public static GetVpcMetaResponse from(NetworkVpcResult vpcMeta) {
    return new GetVpcMetaResponse(
        vpcMeta.vpcCode(),
        vpcMeta.vpcName(),
        vpcMeta.description(),
        vpcMeta.cidrBlock(),
        vpcMeta.defaultEgressPolicy(),
        vpcMeta.defaultIngressPolicy()
    );
  }
}
