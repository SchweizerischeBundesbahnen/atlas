package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.ParkingLotVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = " ParkingLot")
@RequestMapping("v1/parking-lots")
public interface ParkingLotApiV1 {

  @GetMapping
  List<ParkingLotVersionModel> getParkingLots();

}
