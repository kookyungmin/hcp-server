package net.happykoo.hcp.application.port.out;

import java.util.UUID;
import net.happykoo.hcp.domain.instance.ServerInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetInstanceInfoPort {

  Page<ServerInstance> findPagedInstanceByOwnerIdAndSearchKeyword(
      UUID ownerId,
      String searchKeyword,
      Pageable pageable
  );

  boolean existsByInstanceId(UUID ownerId, UUID instanceId);
}
