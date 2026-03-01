package net.happykoo.hcp.application.port.in;

import java.util.List;
import net.happykoo.hcp.application.port.in.result.InstanceImageResult;
import net.happykoo.hcp.application.port.in.result.InstanceSpecResult;
import net.happykoo.hcp.application.port.in.result.NetworkVpcResult;

public interface FindInstanceMetaUseCase {

  List<InstanceImageResult> getAllImageMeta();

  List<InstanceSpecResult> getAllSpecMeta();

  List<NetworkVpcResult> getAllVpcMeta();
}
