package net.happykoo.hcp.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import net.happykoo.hcp.application.port.out.GetInstanceImagePort;
import net.happykoo.hcp.application.port.out.GetInstanceSpecPort;
import net.happykoo.hcp.application.port.out.GetNetworkVpcPort;
import net.happykoo.hcp.domain.instance.InstanceImage;
import net.happykoo.hcp.domain.instance.InstanceSpec;
import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkVpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstanceMetaServiceTest {

  @Mock
  private GetInstanceImagePort getInstanceImagePort;
  @Mock
  private GetInstanceSpecPort getInstanceSpecPort;
  @Mock
  private GetNetworkVpcPort getNetworkVpcPort;

  @InjectMocks
  private InstanceMetaService instanceMetaService;

  @Test
  @DisplayName("메타 조회 :: image/spec/vpc 메타를 API 결과 타입으로 변환")
  void metaMappingTest1() {
    when(getInstanceImagePort.findAllInstanceImage()).thenReturn(List.of(
        new InstanceImage("img-1", "ubuntu-24", "Ubuntu", "Ubuntu", "24.04")
    ));
    when(getInstanceSpecPort.findAllInstanceSpec()).thenReturn(List.of(
        new InstanceSpec("spec-1", "small", "Small", "2", "4Gi")
    ));
    when(getNetworkVpcPort.findAllNetworkVpc()).thenReturn(List.of(
        new NetworkVpc(
            "vpc-1",
            "default",
            "Default VPC",
            DefaultNetworkPolicy.ALLOW_ALL,
            DefaultNetworkPolicy.DENY_ALL,
            "10.0.0.0/24"
        )
    ));

    assertEquals(1, instanceMetaService.getAllImageMeta().size());
    assertEquals("img-1", instanceMetaService.getAllImageMeta().getFirst().imageCode());
    assertEquals("spec-1", instanceMetaService.getAllSpecMeta().getFirst().specCode());
    assertEquals("vpc-1", instanceMetaService.getAllVpcMeta().getFirst().vpcCode());
    assertEquals(
        DefaultNetworkPolicy.DENY_ALL,
        instanceMetaService.getAllVpcMeta().getFirst().defaultIngressPolicy()
    );
  }
}
