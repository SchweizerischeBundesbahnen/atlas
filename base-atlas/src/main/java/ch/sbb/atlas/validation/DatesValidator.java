package ch.sbb.atlas.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

public interface DatesValidator {

  LocalDate minDate = LocalDate.of(1699, 12, 31);
  LocalDate maxDate = LocalDate.of(10000, 1, 1);

  LocalDate getValidFrom();

  LocalDate getValidTo();

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "validTo must not be before validFrom")
  default boolean isValidToEqualOrGreaterThenValidFrom() {
    if (getValidTo() == null || getValidFrom() == null) {
      return false;
    }
    return !getValidTo().isBefore(getValidFrom());
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "ValidTo must be between 1.1.1700 and 31.12.9999")
  default boolean isValidToValid() {
    if (getValidTo() == null || getValidFrom() == null) {
      return false;
    }
    return getValidTo().isAfter(minDate) && getValidTo().isBefore(maxDate);
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "ValidFrom must be between 1.1.1700 and 31.12.9999")
  default boolean isValidFromValid() {
    if (getValidTo() == null || getValidFrom() == null) {
      return false;
    }
    return getValidFrom().isAfter(minDate) && getValidFrom().isBefore(maxDate);
  }
}
