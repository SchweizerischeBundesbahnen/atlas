package ch.sbb.line.directory.api;

import java.time.LocalDate;
import javax.validation.constraints.AssertTrue;

public interface SequenctialValidRange {

  LocalDate getValidFrom();

  LocalDate getValidTo();

  @AssertTrue(message = "validTo must not be before validFrom")
  default boolean isValidToAtLeastValidFrom() {
    return !getValidTo().isBefore(getValidFrom());
  }
}
