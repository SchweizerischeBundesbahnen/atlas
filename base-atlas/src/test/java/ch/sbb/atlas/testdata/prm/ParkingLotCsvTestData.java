package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotCsvTestData {

  public static ParkingLotCsvModelContainer getContainer() {
    return ParkingLotCsvModelContainer.builder()
        .sloid(getCsvModel().getSloid())
        .csvModels(List.of(getCsvModel()))
        .build();
  }

  public static ParkingLotCsvModel getCsvModel() {
    return ParkingLotCsvModel.builder()
        .sloid("ch:1:sloid:294:787306")
        .dsSloid("ch:1:sloid:294")
        .didokCode(85002949)
        .status(1)
        .info("Additional Info Example")
        .placesAvailable(0)
        .prmPlacesAvailable(0)
        .validFrom(LocalDate.of(2020, 8, 25))
        .validTo(LocalDate.of(2025, 12, 31))
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();
  }

}
