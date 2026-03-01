package net.happykoo.hcp.domain.instance;

import lombok.Getter;

@Getter
public class InstanceSpec {

  private String specCode;
  private String specName;
  private String specDescription;
  private String cpu;
  private String memory;
}
