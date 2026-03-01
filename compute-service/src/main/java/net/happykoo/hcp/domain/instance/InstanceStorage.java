package net.happykoo.hcp.domain.instance;

import lombok.Getter;

@Getter
public class InstanceStorage {

  private String storageType; //HDD, SSD
  private int storageSize; //GiB 단위
}
