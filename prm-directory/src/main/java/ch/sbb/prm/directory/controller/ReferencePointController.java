package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.ReferencePointApiV1;
import ch.sbb.prm.directory.controller.model.create.CreateReferencePointVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadReferencePointVersionModel;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.service.ReferencePointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReferencePointController implements ReferencePointApiV1 {

  private final ReferencePointService referencePointService;

  @Override
  public List<ReadReferencePointVersionModel> getReferencePoints() {
    return referencePointService.getAllReferencePoints().stream().map(ReferencePointVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadReferencePointVersionModel createReferencePoint(CreateReferencePointVersionModel model) {
    ReferencePointVersion referencePointVersion = ReferencePointVersionMapper.toEntity(model);
    ReferencePointVersion savedReferencePointVersion = referencePointService.createReferencePoint(referencePointVersion);
    return ReferencePointVersionMapper.toModel(savedReferencePointVersion);
  }
}
