package ch.sbb.prm.directory.controller.parkinglot;

import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotOverviewModel;
import ch.sbb.prm.directory.api.parkinglot.ParkingLotApiInternal;
import ch.sbb.prm.directory.service.ParkingLotService;
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
