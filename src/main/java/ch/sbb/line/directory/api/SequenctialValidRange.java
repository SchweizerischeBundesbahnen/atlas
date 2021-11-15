package ch.sbb.line.directory.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.AssertTrue;

public interface SequenctialValidRange {

  LocalDate getValidFrom();

  LocalDate getValidTo();

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "validTo must not be before validFrom")
  default boolean isValidToEqualOrGreaterThenValidFrom() {
    return !getValidTo().isBefore(getValidFrom());
  }
}
