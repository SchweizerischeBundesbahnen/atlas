package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateTrafficPointElementVersionModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateSloidForNotEndingColon() {
    CreateTrafficPointElementVersionModel model = getCreateTrafficPointVersionModel();
    model.setSloid("ch:1:sloid::1");

    Set<ConstraintViolation<CreateTrafficPointElementVersionModel>> result = validator.validate(model);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldValidateSloidForEndingColon() {
    CreateTrafficPointElementVersionModel model = getCreateTrafficPointVersionModel();
    model.setSloid("ch:1:sloid::");

    Set<ConstraintViolation<CreateTrafficPointElementVersionModel>> result = validator.validate(model);
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldValidateSloidForAutomaticAssign() {
    CreateTrafficPointElementVersionModel model = getCreateTrafficPointVersionModel();
    model.setSloid(null);

    Set<ConstraintViolation<CreateTrafficPointElementVersionModel>> result = validator.validate(model);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldAllowDesignationEmpty() {
    CreateTrafficPointElementVersionModel model = getCreateTrafficPointVersionModel();
    model.setDesignation("");

    Set<ConstraintViolation<CreateTrafficPointElementVersionModel>> result = validator.validate(model);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldNotAllowDesignationLongerThanFourthy() {
    CreateTrafficPointElementVersionModel model = getCreateTrafficPointVersionModel();
    model.setDesignation("01234567890123456789012345678901234567891");

    Set<ConstraintViolation<CreateTrafficPointElementVersionModel>> result = validator.validate(model);
    assertThat(result).hasSize(1);
  }

  private static CreateTrafficPointElementVersionModel getCreateTrafficPointVersionModel() {
    GeolocationBaseCreateModel trafficPointElementGeolocation = GeolocationBaseCreateModel
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2505236.389)
        .north(1116323.213)
        .height(-9999.0)
        .build();

    return CreateTrafficPointElementVersionModel
        .builder()
        .designation("Bezeichnung")
        .designationOperational("gali00")
        .numberWithoutCheckDigit(1400015)
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .sloid("ch:1:sloid:1400015:0:310240")
        .parentSloid("ch:1:sloid:1400015:310240")
        .compassDirection(277.0)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 6))
        .validTo(LocalDate.of(2099, 12, 31))
        .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .editor("fs45117")
        .build();
  }

}