package net.happykoo.hcp.domain.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstanceStorage {

  private String storageType; //HDD, SSD
  private int storageSize; //GiB 단위
}
