package net.happykoo.hcp.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NetworkDomainTest {

  @Test
  @DisplayName("NetworkVpc/NetworkPolicy :: 생성자 값이 그대로 보존")
  void constructorTest1() {
    NetworkVpc vpc = new NetworkVpc(
        "vpc-1",
        "default",
        "Default VPC",
        DefaultNetworkPolicy.ALLOW_ALL,
        DefaultNetworkPolicy.DENY_ALL,
        "10.0.0.0/24"
    );
    NetworkPolicy policy = new NetworkPolicy(
        UUID.randomUUID(),
        NetworkPolicyType.INGRESS,
        "ssh",
        "0.0.0.0/0",
        "22"
    );

    assertEquals("vpc-1", vpc.getVpcCode());
    assertEquals(DefaultNetworkPolicy.ALLOW_ALL, vpc.getDefaultEgressPolicy());
    assertEquals(NetworkPolicyType.INGRESS, policy.getType());
    assertEquals("22", policy.getPort());
  }

  @Test
  @DisplayName("NetworkVpc 축약 생성자 :: vpcCode 만 설정")
  void constructorTest2() {
    assertEquals("vpc-1", new NetworkVpc("vpc-1").getVpcCode());
  }
}
