package net.happykoo.hcp.adapter.in.web;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.auth.ServerInstanceWritePermission;
import net.happykoo.hcp.adapter.in.web.response.GetImageMetaResponse;
import net.happykoo.hcp.adapter.in.web.response.GetMetaAllResponse;
import net.happykoo.hcp.adapter.in.web.response.GetSpecMetaResponse;
import net.happykoo.hcp.adapter.in.web.response.GetVpcMetaResponse;
import net.happykoo.hcp.application.port.in.FindInstanceMetaUseCase;
import net.happykoo.hcp.common.web.annotation.WebAdapter;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/v1/instance/meta")
@RequiredArgsConstructor
public class InstanceMetaController {

  private final FindInstanceMetaUseCase findInstanceMetaUseCase;

  @GetMapping("/all")
  @ServerInstanceWritePermission
  public CommonResponseEntity<GetMetaAllResponse> getInstanceMetaData() {
    var response = new GetMetaAllResponse(
        GetImageMetaResponse.from(findInstanceMetaUseCase.getAllImageMeta()),
        GetSpecMetaResponse.from(findInstanceMetaUseCase.getAllSpecMeta()),
        GetVpcMetaResponse.from(findInstanceMetaUseCase.getAllVpcMeta())
    );
    return CommonResponseEntity.ok(response);
  }

}
