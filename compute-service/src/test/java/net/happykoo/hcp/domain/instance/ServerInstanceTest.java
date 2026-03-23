package net.happykoo.hcp.domain.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkVpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServerInstanceTest {

  @Test
  @DisplayName("ServerInstance :: 연관 객체의 축약 getter 와 변경 메서드가 정상 동작")
  void projectionAndMutationTest1() {
    ServerInstance instance = new ServerInstance(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "web-1",
        new HashSet<>(Set.of("blue")),
        new InstanceImage("img-1", "ubuntu", "Ubuntu", "Ubuntu", "24.04"),
        new NetworkVpc(
            "vpc-1",
            "default",
            "Default VPC",
            DefaultNetworkPolicy.ALLOW_ALL,
            DefaultNetworkPolicy.ALLOW_SAME_VPC,
            "10.0.0.0/24"
        ),
        new InstanceSpec("spec-1", "small", "Small", "2", "4Gi"),
        new InstanceStorage("SSD", 100),
        InstanceStatus.PROVISIONING
    );

    instance.changeSpec(new InstanceSpec("spec-2", "medium", "Medium", "4", "8Gi"));
    instance.changeStorage(new InstanceStorage("HDD", 150));
    instance.addAllTags(Set.of("green", "prod"));
    instance.updateStatus(InstanceStatus.RUNNING);

    assertEquals("ubuntu", instance.getImageName());
    assertEquals("ALLOW_ALL", instance.getDefaultEgressPolicy());
    assertEquals("4", instance.getCpu());
    assertEquals("HDD", instance.getStorageType());
    assertEquals(150, instance.getStorageSize());
    assertEquals(3, instance.getTags().size());
    assertEquals(InstanceStatus.RUNNING, instance.getStatus());
  }

  @Test
  @DisplayName("ServerInstance :: tags/image/vpc/spec/storage 가 null 이어도 안전한 기본값 반환")
  void projectionAndMutationTest2() {
    ServerInstance instance = new ServerInstance(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "web-1",
        null,
        null,
        null,
        null,
        null,
        InstanceStatus.PROVISIONING
    );

    instance.clearTags();
    instance.addAllTags(Set.of("prod"));

    assertNull(instance.getImageName());
    assertNull(instance.getVpcCode());
    assertNull(instance.getSpecCode());
    assertEquals(0, instance.getCpu() == null ? 0 : 1);
    assertEquals("prod", instance.getTags().iterator().next());
    assertEquals(0, instance.getStorageSize());
  }
}
