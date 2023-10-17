package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.atlas.api.prm.model.stopplace.ReadStopPlaceVersionModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.StopPlaceApiV1;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.mapper.StopPlaceVersionMapper;
import ch.sbb.prm.directory.service.StopPlaceImportService;
import ch.sbb.prm.directory.service.StopPlaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPlaceController implements StopPlaceApiV1 {

  private final StopPlaceService stopPlaceService;
  private final StopPlaceImportService stopPlaceImportService;

  @Override
  public List<ReadStopPlaceVersionModel> getAllStopPaces() {
    return stopPlaceService.getAllStopPlaces().stream().map(StopPlaceVersionMapper::toModel).toList();
  }

  @Override
  public ReadStopPlaceVersionModel createStopPlace(CreateStopPlaceVersionModel stopPlaceVersionModel) {
    StopPlaceVersion stopPlaceVersion = StopPlaceVersionMapper.toEntity(stopPlaceVersionModel);
    StopPlaceVersion savedVersion = stopPlaceService.createStopPlace(stopPlaceVersion);
    return StopPlaceVersionMapper.toModel(savedVersion);
  }

  @Override
  public List<ReadStopPlaceVersionModel> updateStopPlace(Long id, CreateStopPlaceVersionModel stopPlaceVersionModel) {
    StopPlaceVersion stopPlaceVersionToUpdate =
        stopPlaceService.getStopPlaceById(id).orElseThrow(() -> new IdNotFoundException(id));

    StopPlaceVersion editedVersion = StopPlaceVersionMapper.toEntity(stopPlaceVersionModel);
    stopPlaceService.updateStopPlaceVersion(stopPlaceVersionToUpdate, editedVersion);

    return stopPlaceService.findAllByNumberOrderByValidFrom(stopPlaceVersionToUpdate.getNumber()).stream()
        .map(StopPlaceVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importServicePoints(StopPlaceImportRequestModel importRequestModel) {
    return stopPlaceImportService.importServicePoints(importRequestModel.getStopPlaceCsvModelContainers());
  }

}
