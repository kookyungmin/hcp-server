package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.network.NetworkVpc;

public interface GetNetworkVpcPort {

  List<NetworkVpc> findAllNetworkVpc();

}
