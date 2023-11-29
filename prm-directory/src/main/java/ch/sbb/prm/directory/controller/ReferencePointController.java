package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.referencepoint.CreateReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ReferencePointApiV1;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.service.ReferencePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReferencePointController implements ReferencePointApiV1 {

  private final ReferencePointService referencePointService;

  @Override
  public List<ReadReferencePointVersionModel> getReferencePoints() {
    return referencePointService.getAllReferencePoints().stream().map(ReferencePointVersionMapper::toModel).toList();
  }

  @Override
  public ReadReferencePointVersionModel createReferencePoint(CreateReferencePointVersionModel model) {
    ReferencePointVersion referencePointVersion = ReferencePointVersionMapper.toEntity(model);
    ReferencePointVersion savedReferencePointVersion = referencePointService.createReferencePoint(referencePointVersion);
    return ReferencePointVersionMapper.toModel(savedReferencePointVersion);
  }

  @Override
  public List<ReadReferencePointVersionModel> updateReferencePoint(Long id, CreateReferencePointVersionModel model) {
    ReferencePointVersion referencePointVersion =
        referencePointService.getReferencePointById(id).orElseThrow(() -> new IdNotFoundException(id));

    ReferencePointVersion editedVersion = ReferencePointVersionMapper.toEntity(model);
    referencePointService.updateReferencePointVersion(referencePointVersion, editedVersion);

    return referencePointService.getAllVersions(referencePointVersion.getSloid()).stream()
        .map(ReferencePointVersionMapper::toModel).toList();
  }
}
