package net.happykoo.hcp.adapter.in.web.response;

import java.util.List;

public record GetMetaAllResponse(
    List<GetImageMetaResponse> osImageList,
    List<GetSpecMetaResponse> specList,
    List<GetVpcMetaResponse> vpcList
) {

}
