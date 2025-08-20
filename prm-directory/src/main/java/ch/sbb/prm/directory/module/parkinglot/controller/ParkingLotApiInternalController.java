package ch.sbb.prm.directory.module.parkinglot.controller;

import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotOverviewModel;
import ch.sbb.prm.directory.module.parkinglot.api.ParkingLotApiInternal;
import ch.sbb.prm.directory.module.parkinglot.service.ParkingLotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ParkingLotApiInternalController implements ParkingLotApiInternal {

  private final ParkingLotService parkingLotService;

  @Override
  public List<ParkingLotOverviewModel> getParkingLotsOverview(String parentServicePointSloid) {
    return parkingLotService.buildOverview(parkingLotService.findByParentServicePointSloid(parentServicePointSloid));
  }

}
