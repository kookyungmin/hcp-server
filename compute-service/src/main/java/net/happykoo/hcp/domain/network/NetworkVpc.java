package net.happykoo.hcp.domain.network;

public class NetworkVpc {

  private String vpcCode;
  private String name;
  private String description;
  private DefaultNetworkPolicy defaultEgressPolicy;
  private DefaultNetworkPolicy defaultIngressPolicy;
  private String cidrBlock;
}
