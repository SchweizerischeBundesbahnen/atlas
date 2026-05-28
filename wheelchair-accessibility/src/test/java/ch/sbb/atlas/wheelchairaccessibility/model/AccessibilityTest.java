package ch.sbb.atlas.wheelchairaccessibility.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AccessibilityTest {

  @Test
  void shouldMinifyAccessibility() {
    Accessibility accessibility = new Accessibility()
        .with(new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 29)), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), WheelchairAccessibilityState.NO_ACCESS)
        .with(new DateRange(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), WheelchairAccessibilityState.NO_ACCESS);

    Accessibility expectedMinifiedAccessibility = new Accessibility()
        .with(new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 29)), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30)), WheelchairAccessibilityState.NO_ACCESS);

    Accessibility result = accessibility.minify();
    assertThat(result).withRepresentation(AccessibilityRepresentation.INSTANCE).isEqualTo(expectedMinifiedAccessibility);
  }

  @Test
  void shouldMinifyEmptyAccessibility() {
    Accessibility accessibility = new Accessibility();

    Accessibility result = accessibility.minify();
    assertThat(result).withRepresentation(AccessibilityRepresentation.INSTANCE).isEqualTo(accessibility);
  }

  @Test
  void shouldPrettyPrintAccessibility() {
    Accessibility accessibility = new Accessibility().with(new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)),
        WheelchairAccessibilityState.AUTONOMY);

    assertThat(accessibility.prettyPrint()).isEqualToIgnoringNewLines("01.01.2020-31.01.2020: AUTONOMY");
  }
}