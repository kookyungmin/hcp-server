package net.happykoo.hcp.domain.network;

import lombok.Getter;

//TODO: 원래 owner 가 있고, 사용자 별로 관리 되어야 하지만, MVP 상 default VPC 를 이용하는 걸로 가정
@Getter
public class NetworkVpc {

  private String vpcCode;
  private String name;
  private String description;
  private DefaultNetworkPolicy defaultEgressPolicy;
  private DefaultNetworkPolicy defaultIngressPolicy;
  private String cidrBlock;
}
