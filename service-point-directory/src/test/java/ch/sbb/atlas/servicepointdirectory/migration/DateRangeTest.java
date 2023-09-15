package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateRangeTest {

  @Test
  void shouldEvaluateContainsOnFromDate() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    assertThat(dateRange.contains(LocalDate.of(2013, 1, 25))).isTrue();
  }

  @Test
  void shouldEvaluateContainsOnToDate() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    assertThat(dateRange.contains(LocalDate.of(2018, 2, 17))).isTrue();
  }

  @Test
  void shouldEvaluateContainsOnBetween() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    assertThat(dateRange.contains(LocalDate.of(2016, 2, 17))).isTrue();
  }

  @Test
  void shouldEvaluateContainsOnBefore() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    assertThat(dateRange.contains(LocalDate.of(2013, 1, 24))).isFalse();
  }

  @Test
  void shouldEvaluateContainsOnAfter() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    assertThat(dateRange.contains(LocalDate.of(2018, 2, 18))).isFalse();
  }

  @Test
  void shouldEvaluateDateRangeDoesNotOverlapIfBefore() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    DateRange other = DateRange.builder().from(LocalDate.of(2012, 1, 25)).to(LocalDate.of(2012, 2, 17)).build();
    assertThat(dateRange.overlapsWith(other)).isFalse();
  }

  @Test
  void shouldEvaluateDateRangeOverlapIfValidFromOverlaps() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    DateRange other = DateRange.builder().from(LocalDate.of(2012, 1, 25)).to(LocalDate.of(2013, 1, 25)).build();
    assertThat(dateRange.overlapsWith(other)).isTrue();
  }

  @Test
  void shouldEvaluateDateRangeOverlapIfValidToOverlaps() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    DateRange other = DateRange.builder().from(LocalDate.of(2018, 2, 17)).to(LocalDate.of(2019, 1, 25)).build();
    assertThat(dateRange.overlapsWith(other)).isTrue();
  }

  @Test
  void shouldEvaluateDateRangeOverlapIfBetween() {
    DateRange dateRange = DateRange.builder().from(LocalDate.of(2013, 1, 25)).to(LocalDate.of(2018, 2, 17)).build();
    DateRange other = DateRange.builder().from(LocalDate.of(2014, 2, 17)).to(LocalDate.of(2015, 1, 25)).build();
    assertThat(dateRange.overlapsWith(other)).isTrue();
  }

}
