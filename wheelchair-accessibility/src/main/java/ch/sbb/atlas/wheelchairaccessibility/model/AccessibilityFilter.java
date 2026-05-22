package ch.sbb.atlas.wheelchairaccessibility.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AccessibilityFilter {

  private static final int ACCESSIBILITY_DAYS_TO_CALCULATE = 30;

  private final LocalDate from;

  private final int days;

  public AccessibilityFilter(LocalDate from) {
    this(from, ACCESSIBILITY_DAYS_TO_CALCULATE);
  }

  public AccessibilityFilter(LocalDate from, int days) {
    this.from = from;
    this.days = days;
  }
}
