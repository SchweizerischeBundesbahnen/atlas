package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotImportRequestModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/parking-lots")
public interface ParkingLotApiV1 {

  @GetMapping
  List<ReadParkingLotVersionModel> getParkingLots();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadParkingLotVersionModel createParkingLot(@RequestBody @Valid ParkingLotVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadParkingLotVersionModel> updateParkingLot(@PathVariable Long id,
      @RequestBody @Valid ParkingLotVersionModel model);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importParkingLots(@RequestBody @Valid ParkingLotImportRequestModel importRequestModel);
}
