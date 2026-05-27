package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccessibilityRangesCalculatorTest {

  @Test
  void shouldGetAccessibilityRangesForOneVersion() {
    DateRange dateRange = new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(List.of(dateRange))
        .getDateRanges();

    List<DateRange> expectedAccessibilityRanges = List.of(
        dateRange
    );

    assertThat(accessibilityRanges).isEqualTo(expectedAccessibilityRanges);
  }

  @Test
  void shouldGetAccessibilityRangesForOneVersionUntilMaxDate() {
    DateRange dateRange = new DateRange(LocalDate.of(2020, 1, 1), VersioningData.MAX_DATE);

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(List.of(dateRange))
        .getDateRanges();

    List<DateRange> expectedAccessibilityRanges = List.of(
        new DateRange(LocalDate.of(2020, 1, 1), VersioningData.MAX_DATE)
    );

    assertThat(accessibilityRanges).isEqualTo(expectedAccessibilityRanges);
  }

  @Test
  void shouldGetAccessibilityRangesForTwoVersionsWithGap() {
    DateRange version1 = new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
    DateRange version2 = new DateRange(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31));

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(List.of(version1, version2))
        .getDateRanges();

    List<DateRange> expectedAccessibilityRanges = List.of(
        version1,
        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)),
        version2
    );

    assertThat(accessibilityRanges).isEqualTo(expectedAccessibilityRanges);
  }

  @Test
  void shouldGetAccessibilityRangesForTwoVersionsWithOverlap() {
    DateRange version1 = new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
    DateRange version2 = new DateRange(LocalDate.of(2020, 10, 1), LocalDate.of(2025, 12, 31));

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(List.of(version1, version2))
        .getDateRanges();

    List<DateRange> expectedAccessibilityRanges = List.of(
        new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 9, 30)),
        new DateRange(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 12, 31)),
        new DateRange(LocalDate.of(2021, 1, 1), LocalDate.of(2025, 12, 31))
    );

    assertThat(accessibilityRanges).isEqualTo(expectedAccessibilityRanges);
  }

  @Test
  void shouldGetAccessibilityRangesForTwoVersionsWithOverlapOfOneDayWithin() {
    DateRange version1 = new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
    DateRange version2 = new DateRange(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 1));

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(List.of(version1, version2))
        .getDateRanges();

    List<DateRange> expectedAccessibilityRanges = List.of(
        new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 9, 30)),
        new DateRange(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 1)),
        new DateRange(LocalDate.of(2020, 10, 2), LocalDate.of(2020, 12, 31))
    );

    assertThat(accessibilityRanges).isEqualTo(expectedAccessibilityRanges);
  }
}