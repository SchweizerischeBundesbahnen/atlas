package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.api.prm.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion.ParkingLotVersionBuilder;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
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

  public static ParkingLotVersionBuilder<?, ?> builderVersion1(){
    return ParkingLotVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation")
        .info("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED);
  }

  public static ParkingLotVersionBuilder<?, ?> builderVersion2(){
    return ParkingLotVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation wrong")
        .info("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED);
  }

  public static ParkingLotVersionBuilder<?, ?> builderVersion3(){
    return ParkingLotVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation ok")
        .info("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED);
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
