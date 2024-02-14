package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ParkingLotApiV1;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.service.ParkingLotService;
import ch.sbb.prm.directory.service.dataimport.ParkingLotImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ParkingLotsController implements ParkingLotApiV1 {

  private final ParkingLotService parkingLotService;
  private final ParkingLotImportService parkingLotImportService;

  @Override
  public List<ReadParkingLotVersionModel> getParkingLots() {
    return parkingLotService.getAllParkingLots().stream().map(ParkingLotVersionMapper::toModel).toList();
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
