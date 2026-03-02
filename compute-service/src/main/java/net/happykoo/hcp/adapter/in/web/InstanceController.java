package net.happykoo.hcp.adapter.in.web;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.request.ProvisionInstanceRequest;
import net.happykoo.hcp.adapter.in.web.resolver.IdempotencyKey;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.common.web.annotation.CurrentActor;
import net.happykoo.hcp.common.web.annotation.WebAdapter;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import net.happykoo.hcp.common.web.security.Actor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/v1/instance")
@RequiredArgsConstructor
public class InstanceController {

  private final ProvisionInstanceUseCase provisionInstanceUseCase;

  @PostMapping("/provisioning")
  public CommonResponseEntity<Void> provisionInstance(
      @IdempotencyKey String idempotencyKey,
      @RequestBody ProvisionInstanceRequest request,
      @CurrentActor Actor actor
  ) {
    provisionInstanceUseCase.provisionInstance(new ProvisionInstanceCommand(
        UUID.fromString(actor.userId()),
        request.name(),
        request.tags(),
        request.imageCode(),
        request.vpcCode(),
        request.specCode(),
        request.storageType(),
        request.storageSize(),
        idempotencyKey
    ));

    return CommonResponseEntity.ok();
  }
}
