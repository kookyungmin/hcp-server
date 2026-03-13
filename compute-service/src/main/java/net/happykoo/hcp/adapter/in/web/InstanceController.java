package net.happykoo.hcp.adapter.in.web;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.auth.ServerInstanceReadPermission;
import net.happykoo.hcp.adapter.in.web.auth.ServerInstanceWritePermission;
import net.happykoo.hcp.adapter.in.web.request.ProvisionInstanceRequest;
import net.happykoo.hcp.adapter.in.web.request.RegisterSshKey;
import net.happykoo.hcp.adapter.in.web.request.UpdateInstanceLifecycleRequest;
import net.happykoo.hcp.adapter.in.web.request.UpdateInstanceSpecRequest;
import net.happykoo.hcp.adapter.in.web.request.UpdateInstanceTagRequest;
import net.happykoo.hcp.adapter.in.web.resolver.IdempotencyKey;
import net.happykoo.hcp.adapter.in.web.response.GetInstanceListResponse;
import net.happykoo.hcp.adapter.in.web.response.GetInstanceResponse;
import net.happykoo.hcp.adapter.in.web.response.GetSshKeyResponse;
import net.happykoo.hcp.application.port.in.FindInstanceSshKeyUseCase;
import net.happykoo.hcp.application.port.in.FindInstanceUseCase;
import net.happykoo.hcp.application.port.in.ProvisionInstanceUseCase;
import net.happykoo.hcp.application.port.in.SaveInstanceSshKeyUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceLifecycleUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceSpecUseCase;
import net.happykoo.hcp.application.port.in.UpdateInstanceTagUseCase;
import net.happykoo.hcp.application.port.in.command.FindInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.FindPagedInstanceCommand;
import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;
import net.happykoo.hcp.application.port.in.command.RegisterInstanceSshKeyCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceLifecycleCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceSpecCommand;
import net.happykoo.hcp.application.port.in.command.UpdateInstanceTagCommand;
import net.happykoo.hcp.common.annotation.CurrentActor;
import net.happykoo.hcp.common.annotation.WebAdapter;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import net.happykoo.hcp.common.web.security.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@WebAdapter
@RequestMapping("/v1/instance")
@RequiredArgsConstructor
public class InstanceController {

  private final ProvisionInstanceUseCase provisionInstanceUseCase;
  private final FindInstanceUseCase findInstanceUseCase;
  private final UpdateInstanceLifecycleUseCase updateInstanceLifecycleUseCase;
  private final UpdateInstanceSpecUseCase updateInstanceSpecUseCase;
  private final UpdateInstanceTagUseCase updateInstanceTagUseCase;
  private final FindInstanceSshKeyUseCase findInstanceSshKeyUseCase;
  private final SaveInstanceSshKeyUseCase saveInstanceSshKeyUseCase;

  @PostMapping("/provisioning")
  @ServerInstanceWritePermission
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

  @PostMapping("/stop")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> stopInstance(
      @IdempotencyKey String idempotencyKey,
      @RequestBody UpdateInstanceLifecycleRequest request,
      @CurrentActor Actor actor
  ) {
    updateInstanceLifecycleUseCase.stopInstance(new UpdateInstanceLifecycleCommand(
        UUID.fromString(request.instanceId()),
        UUID.fromString(actor.userId()),
        idempotencyKey
    ));

    return CommonResponseEntity.ok();
  }

  @PostMapping("/restart")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> restartInstance(
      @IdempotencyKey String idempotencyKey,
      @RequestBody UpdateInstanceLifecycleRequest request,
      @CurrentActor Actor actor
  ) {
    updateInstanceLifecycleUseCase.restartInstance(new UpdateInstanceLifecycleCommand(
        UUID.fromString(request.instanceId()),
        UUID.fromString(actor.userId()),
        idempotencyKey
    ));

    return CommonResponseEntity.ok();
  }

  @PostMapping("/terminate")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> terminateInstance(
      @IdempotencyKey String idempotencyKey,
      @RequestBody UpdateInstanceLifecycleRequest request,
      @CurrentActor Actor actor
  ) {
    updateInstanceLifecycleUseCase.terminateInstance(new UpdateInstanceLifecycleCommand(
        UUID.fromString(request.instanceId()),
        UUID.fromString(actor.userId()),
        idempotencyKey
    ));

    return CommonResponseEntity.ok();
  }

  @PatchMapping("/tag")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> updateInstanceTags(
      @CurrentActor Actor actor,
      @RequestBody UpdateInstanceTagRequest request
  ) {
    updateInstanceTagUseCase.updateInstanceTag(
        new UpdateInstanceTagCommand(
            UUID.fromString(actor.userId()),
            UUID.fromString(request.instanceId()),
            request.tags()
        )
    );
    return CommonResponseEntity.ok();
  }

  @PatchMapping("/spec")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> updateInstanceSpec(
      @CurrentActor Actor actor,
      @RequestBody UpdateInstanceSpecRequest request,
      @IdempotencyKey String idempotencyKey
  ) {
    updateInstanceSpecUseCase.updateInstanceSpec(
        new UpdateInstanceSpecCommand(
            UUID.fromString(actor.userId()),
            UUID.fromString(request.instanceId()),
            idempotencyKey,
            request.specCode(),
            request.storageType(),
            request.storageSize()
        )
    );
    return CommonResponseEntity.ok();
  }

  @PostMapping("/ssh-key")
  @ServerInstanceWritePermission
  public CommonResponseEntity<Void> updateInstanceSshKey(
      @CurrentActor Actor actor,
      @RequestBody RegisterSshKey request,
      @IdempotencyKey String idempotencyKey

  ) {
    saveInstanceSshKeyUseCase.saveInstanceSshKey(
        new RegisterInstanceSshKeyCommand(
            UUID.fromString(request.instanceId()),
            UUID.fromString(actor.userId()),
            idempotencyKey,
            request.keyName(),
            request.sshKey()
        ));
    return CommonResponseEntity.ok();
  }

  @GetMapping("/ssh-key/{instanceId}")
  @ServerInstanceReadPermission
  public CommonResponseEntity<GetSshKeyResponse> getInstanceSshKey(
      @PathVariable String instanceId,
      @CurrentActor Actor actor
  ) {
    var sshPublicKey = findInstanceSshKeyUseCase.findInstanceSshKey(
        new FindInstanceSshKeyCommand(
            UUID.fromString(instanceId),
            UUID.fromString(actor.userId())
        )
    );
    return CommonResponseEntity.ok(new GetSshKeyResponse(
        sshPublicKey.getInstanceId(),
        sshPublicKey.getName(),
        sshPublicKey.getKey()
    ));
  }

  @GetMapping("/list")
  @ServerInstanceReadPermission
  public CommonResponseEntity<Page<GetInstanceListResponse>> listInstance(
      @CurrentActor Actor actor,
      @RequestParam(required = false) String searchKeyword,
      Pageable pageable
  ) {
    var result = findInstanceUseCase.findPagedInstanceByOwnerIdAndSearchKeyword(
            new FindPagedInstanceCommand(
                UUID.fromString(actor.userId()),
                searchKeyword,
                pageable
            ))
        .map(GetInstanceListResponse::from);
    return CommonResponseEntity.ok(result);
  }

  @GetMapping("/info/{instanceId}")
  @ServerInstanceReadPermission
  public CommonResponseEntity<GetInstanceResponse> listInstance(
      @PathVariable String instanceId,
      @CurrentActor Actor actor
  ) {
    var result = findInstanceUseCase.findInstanceInfo(
        UUID.fromString(instanceId),
        UUID.fromString(actor.userId())
    );
    return CommonResponseEntity.ok(GetInstanceResponse.from(result));
  }
}
