package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.PermissionCode;

public interface GetPermissionPort {

  List<PermissionCode> getAllPermissions();

}
