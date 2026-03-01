package net.happykoo.hcp.application.port.in.result;

import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;

public record NetworkVpcResult(
    String vpcCode,
    String vpcName,
    String description,
    String cidrBlock,
    DefaultNetworkPolicy defaultEgressPolicy,
    DefaultNetworkPolicy defaultIngressPolicy
) {

}
