package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ParkingLotApiV1;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.search.ParkingLotSearchRestrictions;
import ch.sbb.prm.directory.service.ParkingLotService;
import ch.sbb.prm.directory.service.dataimport.ParkingLotImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ParkingLotsController implements ParkingLotApiV1 {

  private final ParkingLotService parkingLotService;
  private final ParkingLotImportService parkingLotImportService;

  @Override
  public Container<ReadParkingLotVersionModel> getParkingLots(Pageable pageable,
      PrmObjectRequestParams prmObjectRequestParams) {
    ParkingLotSearchRestrictions searchRestrictions = ParkingLotSearchRestrictions.builder()
        .pageable(pageable)
        .prmObjectRequestParams(prmObjectRequestParams)
        .build();

    Page<ParkingLotVersion> platformVersions = parkingLotService.findAll(searchRestrictions);

    return Container.<ReadParkingLotVersionModel>builder()
        .objects(platformVersions.stream().map(ParkingLotVersionMapper::toModel).toList())
        .totalCount(platformVersions.getTotalElements())
        .build();
  }

  @Override
  public Container<ReadParkingLotVersionModel> getParkingLotsOverview(Pageable pageable, String parentServicePointSloid) {
    return parkingLotService.buildOverview(parkingLotService.findByParentServicePointSloid(parentServicePointSloid),
        pageable);
  }

  @Override
  public List<ReadParkingLotVersionModel> getParkingLotVersions(String sloid) {
    return parkingLotService.getAllVersions(sloid).stream().map(ParkingLotVersionMapper::toModel).toList();
  }

  @Override
  public ReadParkingLotVersionModel createParkingLot(ParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion = parkingLotService.createParkingLot(ParkingLotVersionMapper.toEntity(model));
    return ParkingLotVersionMapper.toModel(parkingLotVersion);
  }

  @Override
  public List<ReadParkingLotVersionModel> updateParkingLot(Long id, ParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion =
        parkingLotService.getPlatformVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    ParkingLotVersion editedVersion = ParkingLotVersionMapper.toEntity(model);
    parkingLotService.updateParkingLotVersion(parkingLotVersion, editedVersion);

    return parkingLotService.getAllVersions(parkingLotVersion.getSloid()).stream()
        .map(ParkingLotVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importParkingLots(ParkingLotImportRequestModel importRequestModel) {
    return parkingLotImportService.importParkingLots(importRequestModel.getParkingLotCsvModelContainers());
  }

}
