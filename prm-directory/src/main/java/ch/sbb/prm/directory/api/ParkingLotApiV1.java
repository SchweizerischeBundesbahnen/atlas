package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.controller.model.parkinglot.ReadParkingLotVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/parking-lots")
public interface ParkingLotApiV1 {

  @GetMapping
  List<ReadParkingLotVersionModel> getParkingLots();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadParkingLotVersionModel createParkingLot(@RequestBody @Valid CreateParkingLotVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(path = "{id}")
  List<ReadParkingLotVersionModel> updateReferencePoint(@PathVariable Long id,
      @RequestBody @Valid CreateParkingLotVersionModel model);
}
