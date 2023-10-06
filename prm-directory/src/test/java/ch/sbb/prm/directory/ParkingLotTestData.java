package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotTestData {

  public static ParkingLotVersion getParkingLotVersion(){
    return ParkingLotVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation")
        .info("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();
  }

  public static CreateParkingLotVersionModel getCreateParkingLotVersionModel(){
    return CreateParkingLotVersionModel.builder()
        .sloid("ch:1.sloid:12345:1")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation")
        .info("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();
  }

}
