package net.happykoo.hcp.application.port;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.FindInstanceMetaUseCase;
import net.happykoo.hcp.application.port.in.result.InstanceImageResult;
import net.happykoo.hcp.application.port.in.result.InstanceSpecResult;
import net.happykoo.hcp.application.port.in.result.NetworkVpcResult;
import net.happykoo.hcp.application.port.out.GetInstanceImagePort;
import net.happykoo.hcp.application.port.out.GetInstanceSpecPort;
import net.happykoo.hcp.application.port.out.GetNetworkVpcPort;
import net.happykoo.hcp.common.web.annotation.UseCase;

@UseCase
@RequiredArgsConstructor
public class InstanceMetaService implements FindInstanceMetaUseCase {

  private final GetInstanceImagePort getInstanceImageMetaPort;
  private final GetInstanceSpecPort getInstanceSpecPort;
  private final GetNetworkVpcPort getNetworkVpcPort;

  @Override
  public List<InstanceImageResult> getAllImageMeta() {
    return getInstanceImageMetaPort.findAllInstanceImage()
        .stream()
        .map(image -> new InstanceImageResult(
            image.getImageCode(),
            image.getOsName(),
            image.getOsVersion(),
            image.getImageDescription()
        ))
        .toList();
  }

  @Override
  public List<InstanceSpecResult> getAllSpecMeta() {
    return getInstanceSpecPort.findAllInstanceSpec()
        .stream()
        .map(spec -> new InstanceSpecResult(
            spec.getSpecCode(),
            spec.getSpecName(),
            spec.getSpecDescription()
        ))
        .toList();
  }

  @Override
  public List<NetworkVpcResult> getAllVpcMeta() {
    return getNetworkVpcPort.findAllNetworkVpc()
        .stream()
        .map(vpc -> new NetworkVpcResult(
            vpc.getVpcCode(),
            vpc.getName(),
            vpc.getDescription(),
            vpc.getCidrBlock(),
            vpc.getDefaultEgressPolicy(),
            vpc.getDefaultIngressPolicy())
        )
        .toList();
  }
}
