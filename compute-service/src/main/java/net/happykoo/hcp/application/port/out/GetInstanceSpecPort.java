package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.instance.InstanceSpec;

public interface GetInstanceSpecPort {

  List<InstanceSpec> findAllInstanceSpec();

}
