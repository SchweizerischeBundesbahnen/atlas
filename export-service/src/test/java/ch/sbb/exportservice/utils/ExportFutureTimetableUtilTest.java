package ch.sbb.exportservice.utils;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExportFutureTimetableUtilTest {

  @Test
  void shouldHaveCorrectDateRangeForTimetableYearsExportOnMarch2025() {
    DateRange result = ExportFutureTimetableUtil.getTimetableYearsDateRange(LocalDate.of(2025, 3, 18));
    assertThat(result.getFrom()).isEqualTo(LocalDate.of(2024, 12, 15));
    assertThat(result.getTo()).isEqualTo(LocalDate.of(2026, 12, 12));
  }

  @Test
  void shouldHaveCorrectDateRangeForTimetableYearsExportOnDecember2025() {
    DateRange result = ExportFutureTimetableUtil.getTimetableYearsDateRange(LocalDate.of(2025, 12, 18));
    assertThat(result.getFrom()).isEqualTo(LocalDate.of(2025, 12, 14));
    assertThat(result.getTo()).isEqualTo(LocalDate.of(2027, 12, 11));


    result = ExportFutureTimetableUtil.getTimetableYearsDateRange(LocalDate.of(2025, 12, 15));
    assertThat(result.getFrom()).isEqualTo(LocalDate.of(2025, 12, 14));
    assertThat(result.getTo()).isEqualTo(LocalDate.of(2027, 12, 11));
  }
}