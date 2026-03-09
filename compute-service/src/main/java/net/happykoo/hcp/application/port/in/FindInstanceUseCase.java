package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.FindPagedInstanceCommand;
import net.happykoo.hcp.domain.instance.ServerInstance;
import org.springframework.data.domain.Page;

public interface FindInstanceUseCase {

  Page<ServerInstance> findPagedInstanceByOwnerIdAndSearchKeyword(FindPagedInstanceCommand command);

}
