package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccessibilityRangesFilterTest {

  @Test
  void shouldFilterOneVersion() {
    List<DateRange> allRanges = List.of(
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31))
    );

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2019, 2, 1), 30);
    List<DateRange> filteredResult = new AccessibilityRangesFilter(accessibilityFilter).applyTo(allRanges);

    List<DateRange> expectedResult = List.of(
        new DateRange(LocalDate.of(2019, 2, 1), LocalDate.of(2019, 3, 3))
    );
    assertThat(filteredResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldFilterMultipleVersion() {
    List<DateRange> allRanges = List.of(
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 1, 5)),
        new DateRange(LocalDate.of(2019, 1, 6), LocalDate.of(2019, 1, 25)),
        new DateRange(LocalDate.of(2019, 1, 26), LocalDate.of(2019, 2, 15))
    );

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2019, 1, 4), 30);
    List<DateRange> filteredResult = new AccessibilityRangesFilter(accessibilityFilter).applyTo(allRanges);

    List<DateRange> expectedResult = List.of(
        new DateRange(LocalDate.of(2019, 1, 4), LocalDate.of(2019, 1, 5)),
        new DateRange(LocalDate.of(2019, 1, 6), LocalDate.of(2019, 1, 25)),
        new DateRange(LocalDate.of(2019, 1, 26), LocalDate.of(2019, 2, 3))
    );
    assertThat(filteredResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldFilterWithFirstVersionStartingInFilter() {
    List<DateRange> allRanges = List.of(
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31))
    );

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2018, 12, 15), 30);
    List<DateRange> filteredResult = new AccessibilityRangesFilter(accessibilityFilter).applyTo(allRanges);

    List<DateRange> expectedResult = List.of(
        new DateRange(LocalDate.of(2018, 12, 15), LocalDate.of(2018, 12, 31)),
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 1, 14))
    );
    assertThat(filteredResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldFilterWithLastVersionEndingInFilter() {
    List<DateRange> allRanges = List.of(
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31))
    );

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2019, 12, 15), 30);
    List<DateRange> filteredResult = new AccessibilityRangesFilter(accessibilityFilter).applyTo(allRanges);

    List<DateRange> expectedResult = List.of(
        new DateRange(LocalDate.of(2019, 12, 15), LocalDate.of(2019, 12, 31)),
        new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 14))
    );
    assertThat(filteredResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldFilterWithNoVersion() {
    List<DateRange> allRanges = List.of(
        new DateRange(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31))
    );

    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(LocalDate.of(2000, 2, 1), 30);
    List<DateRange> filteredResult = new AccessibilityRangesFilter(accessibilityFilter).applyTo(allRanges);

    List<DateRange> expectedResult = List.of();
    assertThat(filteredResult).isEqualTo(expectedResult);
  }
}