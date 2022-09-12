package ch.sbb.atlas.base.service.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.AssertTrue;

public interface DatesValidator {

  LocalDate minDate = LocalDate.of(1699, 12, 31);
  LocalDate maxDate = LocalDate.of(10000, 1, 1);

  LocalDate getValidFrom();

  LocalDate getValidTo();

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "validTo must not be before validFrom")
  default boolean isValidToEqualOrGreaterThenValidFrom() {
    return !getValidTo().isBefore(getValidFrom());
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "ValidTo must be between 1.1.1700 and 31.12.9999")
  default boolean isValidToValid() {
    return getValidTo().isAfter(minDate) && getValidTo().isBefore(maxDate);
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "ValidFrom must be between 1.1.1700 and 31.12.9999")
  default boolean isValidFromValid() {
    return getValidFrom().isAfter(minDate) && getValidFrom().isBefore(maxDate);
  }
}
