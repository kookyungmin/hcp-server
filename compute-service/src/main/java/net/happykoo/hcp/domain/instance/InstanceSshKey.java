package net.happykoo.hcp.domain.instance;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InstanceSshKey {

  private UUID instanceId;
  private String name;
  private String key;

}
