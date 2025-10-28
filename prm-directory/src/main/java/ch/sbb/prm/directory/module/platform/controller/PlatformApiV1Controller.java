package ch.sbb.prm.directory.module.platform.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.prm.directory.module.platform.api.PlatformApiV1;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.module.platform.search.PlatformSearchRestrictions;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.search.model.PrmObjectRequestParams;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlatformApiV1Controller implements PlatformApiV1 {

  private final PlatformService platformService;

  @Override
  public Container<ReadPlatformVersionModel> getPlatforms(Pageable pageable, PrmObjectRequestParams prmObjectRequestParams) {
    PlatformSearchRestrictions searchRestrictions = PlatformSearchRestrictions.builder()
        .pageable(pageable)
        .prmObjectRequestParams(prmObjectRequestParams)
        .build();
    Page<PlatformVersion> platformVersions = platformService.findAll(searchRestrictions);

    return Container.<ReadPlatformVersionModel>builder()
        .objects(platformVersions.stream().map(PlatformVersionMapper::toModel).toList())
        .totalCount(platformVersions.getTotalElements())
        .build();
  }

  @Override
  public ReadPlatformVersionModel createPlatform(PlatformVersionModel model) {
    PlatformVersion platformVersion = PlatformVersionMapper.toEntity(model);
    PlatformVersion savedVersion = platformService.createPlatformVersion(platformVersion);
    return PlatformVersionMapper.toModel(savedVersion);
  }

  @Override
  public List<ReadPlatformVersionModel> updatePlatform(Long id, PlatformVersionModel model) {
    PlatformVersion currentVersion = platformService.getPlatformVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    PlatformVersion editedVersion = PlatformVersionMapper.toEntity(model);
    platformService.updatePlatformVersion(currentVersion, editedVersion);

    return platformService.getAllVersions(currentVersion.getSloid()).stream().map(PlatformVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadPlatformVersionModel> getPlatformVersions(String sloid) {
    return platformService.getAllVersions(sloid).stream().map(PlatformVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadPlatformVersionModel> terminatePlatform(String sloid, LocalDate validTo) {
    List<PlatformVersion> currentVersions = platformService.getAllVersions(sloid);

    if (currentVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }

    PlatformVersion currentVersion = currentVersions.getLast();
    platformService.terminate(currentVersion, validTo);

    return platformService.getAllVersions(sloid).stream().map(PlatformVersionMapper::toModel).toList();
  }

}
