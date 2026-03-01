package net.happykoo.hcp.domain.instance;

import lombok.Getter;

@Getter
public class InstanceImage {

  private String imageCode;
  private String imageName;
  private String imageDescription;
  private String osName;
  private String osVersion;

  public InstanceImage(
      String imageCode,
      String imageName,
      String imageDescription,
      String osName,
      String osVersion
  ) {
    this.imageCode = imageCode;
    this.imageName = imageName;
    this.imageDescription = imageDescription;
    this.osName = osName;
    this.osVersion = osVersion;
  }
}
