package ch.sbb.atlas.api.prm.model.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ParkingLotVersionModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateParkingLot() {
    ParkingLotVersionModel model = getParkingLotVersionModel();

    Set<ConstraintViolation<ParkingLotVersionModel>> result = validator.validate(model);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldNotAllowDesignationNull() {
    ParkingLotVersionModel model = getParkingLotVersionModel();
    model.setDesignation(null);

    Set<ConstraintViolation<ParkingLotVersionModel>> result = validator.validate(model);
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldNotAllowDesignationWithMoreThan50Characters() {
    ParkingLotVersionModel model = getParkingLotVersionModel();
    model.setDesignation("012345678901234567890123456789012345678901234567891");

    Set<ConstraintViolation<ParkingLotVersionModel>> result = validator.validate(model);
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldNotAllowNullParentSloid() {
    ParkingLotVersionModel model = getParkingLotVersionModel();
    model.setParentServicePointSloid(null);

    Set<ConstraintViolation<ParkingLotVersionModel>> result = validator.validate(model);
    assertThat(result).hasSize(1);
  }

  public static ParkingLotVersionModel getParkingLotVersionModel() {
    return ParkingLotVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .placesAvailable(BooleanOptionalAttributeType.YES)
        .prmPlacesAvailable(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();
  }

}