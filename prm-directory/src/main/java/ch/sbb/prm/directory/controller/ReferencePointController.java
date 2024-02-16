package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointImportRequestModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ReferencePointApiV1;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.mapper.ReferencePointVersionMapper;
import ch.sbb.prm.directory.search.ReferencePointSearchRestrictions;
import ch.sbb.prm.directory.service.ReferencePointService;
import ch.sbb.prm.directory.service.dataimport.ReferencePointImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReferencePointController implements ReferencePointApiV1 {

  private final ReferencePointService referencePointService;
  private final ReferencePointImportService referencePointImportService;

  @Override
  public Container<ReadReferencePointVersionModel> getReferencePoints(Pageable pageable,
      PrmObjectRequestParams prmObjectRequestParams) {
    ReferencePointSearchRestrictions searchRestrictions = ReferencePointSearchRestrictions.builder()
        .pageable(pageable)
        .prmObjectRequestParams(prmObjectRequestParams)
        .build();

    Page<ReferencePointVersion> platformVersions = referencePointService.findAll(searchRestrictions);

    return Container.<ReadReferencePointVersionModel>builder()
        .objects(platformVersions.stream().map(ReferencePointVersionMapper::toModel).toList())
        .totalCount(platformVersions.getTotalElements())
        .build();
  }

  @Override
  public Container<ReadReferencePointVersionModel> getReferencePointsOverview(Pageable pageable, String parentServicePointSloid) {
    return referencePointService.buildOverview(referencePointService.findByParentServicePointSloid(parentServicePointSloid),
        pageable);
  }

  @Override
  public ReadReferencePointVersionModel createReferencePoint(ReferencePointVersionModel model) {
    ReferencePointVersion referencePointVersion = ReferencePointVersionMapper.toEntity(model);

    ReferencePointVersion savedReferencePointVersion = referencePointService.createReferencePoint(referencePointVersion);
    return ReferencePointVersionMapper.toModel(savedReferencePointVersion);
  }

  @Override
  public List<ReadReferencePointVersionModel> updateReferencePoint(Long id, ReferencePointVersionModel model) {
    ReferencePointVersion referencePointVersion =
        referencePointService.getReferencePointById(id).orElseThrow(() -> new IdNotFoundException(id));

    ReferencePointVersion editedVersion = ReferencePointVersionMapper.toEntity(model);
    referencePointService.updateReferencePointVersion(referencePointVersion, editedVersion);

    return referencePointService.getAllVersions(referencePointVersion.getSloid()).stream()
        .map(ReferencePointVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadReferencePointVersionModel> getReferencePointVersions(String sloid) {
    return referencePointService.getAllVersions(sloid).stream().map(ReferencePointVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importReferencePoints(ReferencePointImportRequestModel importRequestModel) {
    return referencePointImportService.importReferencePoints(importRequestModel.getReferencePointCsvModelContainers());
  }
}
