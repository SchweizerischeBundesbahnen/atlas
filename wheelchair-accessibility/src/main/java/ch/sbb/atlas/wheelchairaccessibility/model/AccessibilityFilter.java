package ch.sbb.atlas.wheelchairaccessibility.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class AccessibilityFilter {

  public static final int ACCESSIBILITY_DAYS_TO_CALCULATE = 30;

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
