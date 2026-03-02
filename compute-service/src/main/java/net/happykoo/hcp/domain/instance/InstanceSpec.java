package net.happykoo.hcp.domain.instance;

import lombok.Getter;

@Getter
public class InstanceSpec {

  private String specCode;
  private String specName;
  private String specDescription;
  private String cpu;
  private String memory;

  public InstanceSpec(
      String specCode,
      String specName,
      String specDescription,
      String cpu,
      String memory
  ) {
    this.specCode = specCode;
    this.specName = specName;
    this.specDescription = specDescription;
    this.cpu = cpu;
    this.memory = memory;
  }

  public InstanceSpec(
      String specCode
  ) {
    this(specCode, null, null, null, null);
  }
}
