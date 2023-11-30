package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.PlatformApiV1;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.service.PlatformService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlatformController implements PlatformApiV1 {

  private final PlatformService platformService;

  @Override
  public List<ReadPlatformVersionModel> getPlatforms() {
    return platformService.getAllPlatforms().stream().map(PlatformVersionMapper::toModel).toList();
  }

  @Override
  public ReadPlatformVersionModel createPlatform(CreatePlatformVersionModel model) {
    PlatformVersion platformVersion = PlatformVersionMapper.toEntity(model);
    PlatformVersion savedVersion = platformService.createPlatformVersion(platformVersion);
    return PlatformVersionMapper.toModel(savedVersion);
  }

  @Override
  public List<ReadPlatformVersionModel> updatePlatform(Long id, CreatePlatformVersionModel model) {
    PlatformVersion platformVersion =
        platformService.getPlatformVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    PlatformVersion editedVersion = PlatformVersionMapper.toEntity(model);
    platformService.updatePlatformVersion(platformVersion, editedVersion);

    return platformService.findAllByNumberOrderByValidFrom(platformVersion.getNumber()).stream()
        .map(PlatformVersionMapper::toModel).toList();

  }

}
