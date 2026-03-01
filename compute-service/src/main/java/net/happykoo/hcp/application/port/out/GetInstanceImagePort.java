package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.instance.InstanceImage;

public interface GetInstanceImagePort {

  List<InstanceImage> findAllInstanceImage();

}
