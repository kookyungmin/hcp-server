package net.happykoo.hcp.domain.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InstanceValueObjectsTest {

  @Test
  @DisplayName("InstanceImage/Spec/Storage/SshKey :: 생성자 값이 그대로 보존")
  void constructorTest1() {
    InstanceImage image = new InstanceImage("img-1", "ubuntu", "Ubuntu", "Ubuntu", "24.04");
    InstanceSpec spec = new InstanceSpec("spec-1", "small", "Small", "2", "4Gi");
    InstanceStorage storage = new InstanceStorage("SSD", 100);
    InstanceSshKey sshKey = new InstanceSshKey(UUID.randomUUID(), "main-key", "ssh-rsa AAAA");

    assertEquals("img-1", image.getImageCode());
    assertEquals("ubuntu", image.getImageName());
    assertEquals("spec-1", spec.getSpecCode());
    assertEquals("2", spec.getCpu());
    assertEquals("SSD", storage.getStorageType());
    assertEquals(100, storage.getStorageSize());
    assertEquals("main-key", sshKey.getName());
    assertEquals("ssh-rsa AAAA", sshKey.getKey());
  }

  @Test
  @DisplayName("InstanceImage/Spec 축약 생성자 :: code 만 설정")
  void constructorTest2() {
    assertEquals("img-1", new InstanceImage("img-1").getImageCode());
    assertEquals("spec-1", new InstanceSpec("spec-1").getSpecCode());
  }
}
