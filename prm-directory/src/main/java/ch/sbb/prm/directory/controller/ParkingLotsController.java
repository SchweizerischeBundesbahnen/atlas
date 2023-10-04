package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.ParkingLotApiV1;
import ch.sbb.prm.directory.controller.model.ParkingLotVersionModel;
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
  public List<ParkingLotVersionModel> getParkingLots() {
    return parkingLotService.getAllParkingLots().stream().map(ParkingLotVersionMapper::toModel).sorted().toList();
  }
}
