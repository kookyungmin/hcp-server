package net.happykoo.hcp.domain.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstanceDomainTest {

  @Test
  @DisplayName("DefaultNetworkPolicy.fromString() :: 대소문자 무관하게 enum 변환")
  void defaultNetworkPolicyTest1() {
    assertEquals(DefaultNetworkPolicy.ALLOW_ALL, DefaultNetworkPolicy.fromString("allow_all"));
    assertEquals(DefaultNetworkPolicy.DENY_ALL, DefaultNetworkPolicy.fromString("DENY_ALL"));
  }

  @Test
  @DisplayName("Instance/InstanceNetworkPolicy :: 생성자 값이 그대로 보존")
  void constructorTest1() {
    UUID instanceId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();

    Instance provisionInstance = new Instance(
        instanceId,
        ownerId,
        "ubuntu-24",
        DefaultNetworkPolicy.ALLOW_ALL,
        DefaultNetworkPolicy.DENY_ALL,
        "10.0.0.0/24",
        "2",
        "4Gi",
        "SSD",
        50
    );
    Instance scaleInstance = new Instance(instanceId, ownerId, "4", "8Gi", "SSD", 100);
    InstanceNetworkPolicy policy = new InstanceNetworkPolicy(
        instanceId,
        NetworkPolicyType.INGRESS,
        "ssh",
        "0.0.0.0/0",
        "22"
    );

    assertEquals("ubuntu-24", provisionInstance.getImageName());
    assertEquals(DefaultNetworkPolicy.ALLOW_ALL, provisionInstance.getDefaultEgressPolicy());
    assertEquals("4", scaleInstance.getCpu());
    assertEquals(100, scaleInstance.getStorageSize());
    assertEquals(NetworkPolicyType.INGRESS, policy.getType());
  }
}
