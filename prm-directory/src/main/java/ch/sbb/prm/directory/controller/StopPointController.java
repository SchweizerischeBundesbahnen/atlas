package ch.sbb.prm.directory.controller;

import static ch.sbb.atlas.servicepoint.Country.SWITZERLAND;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.StopPointApiV1;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ServicePointWithNotSwissCountryNotAllowedException;
import ch.sbb.prm.directory.exception.StopPointAlreadyExistsException;
import ch.sbb.prm.directory.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.search.StopPointSearchRestrictions;
import ch.sbb.prm.directory.service.StopPointService;
import ch.sbb.prm.directory.service.dataimport.StopPointImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointController implements StopPointApiV1 {

  private final StopPointService stopPointService;
  private final StopPointImportService stopPointImportService;

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
    boolean stopPointExisting = stopPointService.isStopPointExisting(stopPointVersionModel.getSloid());
    if (stopPointExisting) {
      throw new StopPointAlreadyExistsException(stopPointVersionModel.getSloid());
    }
    StopPointVersion stopPointVersion = StopPointVersionMapper.toEntity(stopPointVersionModel);
    if(!SWITZERLAND.equals(stopPointVersion.getNumber().getCountry())){
      throw new ServicePointWithNotSwissCountryNotAllowedException(stopPointVersion);
    }
    StopPointVersion savedVersion = stopPointService.save(stopPointVersion);
    return StopPointVersionMapper.toModel(savedVersion);
  }

  @Override
  public List<ReadStopPointVersionModel> updateStopPoint(Long id, CreateStopPointVersionModel stopPointVersionModel) {
    StopPointVersion stopPointVersionToUpdate =
        stopPointService.getStopPointById(id).orElseThrow(() -> new IdNotFoundException(id));

    StopPointVersion editedVersion = StopPointVersionMapper.toEntity(stopPointVersionModel);
    stopPointService.updateStopPointVersion(stopPointVersionToUpdate, editedVersion);

    return stopPointService.findAllByNumberOrderByValidFrom(stopPointVersionToUpdate.getNumber()).stream()
        .map(StopPointVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importStopPoints(StopPointImportRequestModel importRequestModel) {
    return stopPointImportService.importServicePoints(importRequestModel.getStopPointCsvModelContainers());
  }

}
