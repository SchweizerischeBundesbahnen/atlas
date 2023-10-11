package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ParkingLotApiV1;
import ch.sbb.prm.directory.controller.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.controller.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.service.ParkingLotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ParkingLotsController implements ParkingLotApiV1 {

  private final ParkingLotService parkingLotService;

  @Override
  public List<ReadParkingLotVersionModel> getParkingLots() {
    return parkingLotService.getAllParkingLots().stream().map(ParkingLotVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadParkingLotVersionModel createParkingLot(CreateParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion = parkingLotService.createParkingLot(ParkingLotVersionMapper.toEntity(model));
    return ParkingLotVersionMapper.toModel(parkingLotVersion);
  }

  @Override
  public List<ReadParkingLotVersionModel> updateReferencePoint(Long id, CreateParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion =
        parkingLotService.getPlatformVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    ParkingLotVersion editedVersion = ParkingLotVersionMapper.toEntity(model);
    parkingLotService.updateParkingLotVersion(parkingLotVersion, editedVersion);

    return parkingLotService.findAllByNumberOrderByValidFrom(parkingLotVersion.getNumber()).stream()
        .map(ParkingLotVersionMapper::toModel).toList();
  }

}
