package ch.sbb.atlas.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateHelperTest {

  @Test
  void shouldReturnEarlierDateForMin() {
    // given
    LocalDate earlier = LocalDate.of(2026, 1, 1);
    LocalDate later = LocalDate.of(2026, 12, 31);

    // when
    LocalDate result = DateHelper.min(later, earlier);

    // then
    assertThat(result).isEqualTo(earlier);
  }

  @Test
  void shouldReturnLaterDateForMax() {
    // given
    LocalDate earlier = LocalDate.of(2026, 1, 1);
    LocalDate later = LocalDate.of(2026, 12, 31);

    // when
    LocalDate result = DateHelper.max(earlier, later);

    // then
    assertThat(result).isEqualTo(later);
  }

  @Test
  void shouldReturnArgumentForMinWhenDatesAreEqual() {
    // given
    LocalDate same = LocalDate.of(2026, 6, 18);

    // when
    LocalDate result = DateHelper.min(same, same);

    // then
    assertThat(result).isEqualTo(same);
  }

  @Test
  void shouldReturnArgumentForMaxWhenDatesAreEqual() {
    // given
    LocalDate same = LocalDate.of(2026, 6, 18);

    // when
    LocalDate result = DateHelper.max(same, same);

    // then
    assertThat(result).isEqualTo(same);
  }

  @Test
  void shouldFormatDateAsSqlString() {
    // given
    LocalDate date = LocalDate.of(2026, 4, 5);

    // when
    String result = DateHelper.getDateAsSqlString(date);

    // then
    assertThat(result).isEqualTo("2026-04-05");
  }
}

