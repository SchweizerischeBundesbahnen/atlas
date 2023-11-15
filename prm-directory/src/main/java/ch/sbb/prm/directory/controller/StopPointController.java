package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.StopPointApiV1;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointAlreadyExistsException;
import ch.sbb.prm.directory.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.search.StopPointSearchRestrictions;
import ch.sbb.prm.directory.service.SharedServicePointService;
import ch.sbb.prm.directory.service.StopPointService;
import ch.sbb.prm.directory.service.dataimport.StopPointImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointController implements StopPointApiV1 {

  private final StopPointService stopPointService;
  private final StopPointImportService stopPointImportService;
  private final SharedServicePointService sharedServicePointService;

  @Override
  public Container<ReadStopPointVersionModel> getStopPoints(Pageable pageable,
      StopPointRequestParams stopPointRequestParams) {
    StopPointSearchRestrictions searchRestrictions = StopPointSearchRestrictions.builder()
        .pageable(pageable)
        .stopPointRequestParams(stopPointRequestParams)
        .build();
    Page<StopPointVersion> stopPointVersions = stopPointService.findAll(searchRestrictions);

    return Container.<ReadStopPointVersionModel>builder()
        .objects(stopPointVersions.stream().map(StopPointVersionMapper::toModel).toList())
        .totalCount(stopPointVersions.getTotalElements())
        .build();
  }

  @Override
  public ReadStopPointVersionModel createStopPoint(CreateStopPointVersionModel stopPointVersionModel) {
    SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService.findServicePoint(stopPointVersionModel.getSloid()).orElseThrow();
    boolean stopPointExisting = stopPointService.isStopPointExisting(stopPointVersionModel.getSloid());
    if (stopPointExisting) {
      throw new StopPointAlreadyExistsException(stopPointVersionModel.getSloid());
    }
    StopPointVersion stopPointVersion = StopPointVersionMapper.toEntity(stopPointVersionModel);
    StopPointVersion savedVersion = stopPointService.saveAndCheckRights(stopPointVersion, sharedServicePointVersionModel);
    return StopPointVersionMapper.toModel(savedVersion);
  }

  @Override
  public List<ReadStopPointVersionModel> updateStopPoint(Long id, CreateStopPointVersionModel model) {
    StopPointVersion stopPointVersionToUpdate =
        stopPointService.getStopPointById(id).orElseThrow(() -> new IdNotFoundException(id));
    StopPointVersion editedVersion = StopPointVersionMapper.toEntity(model);
    stopPointService.updateStopPointVersion(stopPointVersionToUpdate, editedVersion,
            sharedServicePointService.getSharedServicePointVersionModel(model.getSloid()));

    return stopPointService.findAllByNumberOrderByValidFrom(stopPointVersionToUpdate.getNumber()).stream()
        .map(StopPointVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importStopPoints(StopPointImportRequestModel importRequestModel) {
    return stopPointImportService.importServicePoints(importRequestModel.getStopPointCsvModelContainers());
  }

}
