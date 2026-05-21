package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;

public interface AccessibilityVersion {

  LocalDate getValidFrom();

  LocalDate getValidTo();

  default DateRange toDateRange() {
    return new DateRange(getValidFrom(), getValidTo());
  }
}
