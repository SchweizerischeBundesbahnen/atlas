package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidityTest {

  @Test
  void shouldBeValidValidity() {
    Validity validity = new Validity(new ArrayList<>(List.of(DateRange.builder()
            .from(LocalDate.of(2020, 1, 1))
            .to(LocalDate.of(2020, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2021, 1, 1))
            .to(LocalDate.of(2021, 12, 31))
            .build())));

    assertThat(validity.isNotOverlapping()).isTrue();
  }

  @Test
  void shouldBeInvalidValidity() {
    Validity validity = new Validity(new ArrayList<>(List.of(DateRange.builder()
            .from(LocalDate.of(2020, 1, 1))
            .to(LocalDate.of(2020, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2020, 3, 1))
            .to(LocalDate.of(2021, 12, 31))
            .build())));

    assertThat(validity.isNotOverlapping()).isFalse();
  }

  @Test
  void shouldMergeTwoAdjecentRanges() {
    Validity validity = new Validity(new ArrayList<>(List.of(DateRange.builder()
            .from(LocalDate.of(2020, 1, 1))
            .to(LocalDate.of(2020, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2021, 1, 1))
            .to(LocalDate.of(2021, 12, 31))
            .build())));

    validity = validity.minify();
    assertThat(validity.getDateRanges()).hasSize(1);
    DateRange firstRange = validity.getDateRanges().get(0);
    assertThat(firstRange.getFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstRange.getTo()).isEqualTo(LocalDate.of(2021, 12, 31));
  }

  @Test
  void shouldNotMergeOnGap() {
    Validity validity = new Validity(new ArrayList<>(List.of(DateRange.builder()
            .from(LocalDate.of(2020, 1, 1))
            .to(LocalDate.of(2020, 12, 30))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2021, 1, 1))
            .to(LocalDate.of(2021, 12, 31))
            .build())));

    validity = validity.minify();
    assertThat(validity.getDateRanges()).hasSize(2);
  }

  @Test
  void shouldMergeSomeButNotAll() {
    Validity validity = new Validity(new ArrayList<>(List.of(DateRange.builder()
            .from(LocalDate.of(2020, 1, 1))
            .to(LocalDate.of(2020, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2021, 1, 1))
            .to(LocalDate.of(2021, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2022, 2, 1))
            .to(LocalDate.of(2022, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2023, 1, 1))
            .to(LocalDate.of(2023, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2025, 1, 1))
            .to(LocalDate.of(2025, 12, 31))
            .build())));

    validity = validity.minify();
    assertThat(validity.getDateRanges()).hasSize(3);
  }

  @Test
  void shouldFunctionCorrectlyFor80220137() {
    Validity validity = new Validity(new ArrayList<>(List.of(
        DateRange.builder()
            .from(LocalDate.of(1993, 1, 18))
            .to(LocalDate.of(2000, 2, 15))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2000, 2, 16))
            .to(LocalDate.of(2007, 12, 31))
            .build(),
        DateRange.builder()
            .from(LocalDate.of(2008, 1, 1))
            .to(LocalDate.of(2099, 12, 31))
            .build()
    )));

    validity = validity.minify();
    assertThat(validity.getDateRanges()).hasSize(1);
    assertThat(validity.getDateRanges().get(0).getFrom()).isEqualTo(LocalDate.of(1993, 1, 18));
  }

}
