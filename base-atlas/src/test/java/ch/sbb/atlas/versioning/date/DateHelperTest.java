package ch.sbb.atlas.versioning.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateHelperTest {

  @Test
   void shouldReturnTrueWhenTwoDatesAreSequential() {
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);
    LocalDate next = LocalDate.of(2000, 1, 2);

    //when
    boolean result = DateHelper.areDatesSequential(current, next);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenTwoDatesAreNotSequential() {
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);
    LocalDate next = LocalDate.of(2000, 1, 3);

    //when
    boolean result = DateHelper.areDatesSequential(current, next);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldThrowIllegalStateExceptionWhenCurrentDateIsNull() {
    //given
    LocalDate next = LocalDate.of(2000, 1, 3);

    //when
    assertThatThrownBy(() -> {
      DateHelper.areDatesSequential(null, next);
      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining("Current date is null");

  }

  @Test
   void shouldThrowIllegalStateExceptionWhenNextDateIsNull() {
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);

    //when
    assertThatThrownBy(() -> {
      DateHelper.areDatesSequential(current, null);
      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining("Next date is null");

  }


  @Test
   void shouldReturnMinimumOfTwoEqualsDates() {
    LocalDate date1 = LocalDate.of(2020, 1, 1);
    LocalDate date2 = LocalDate.of(2020, 1, 1);

    LocalDate result = DateHelper.min(date1, date2);
    assertThat(result).isEqualTo(date1);
  }

  @Test
   void shouldReturnMinimumOfTwoDates() {
    LocalDate date1 = LocalDate.of(2020, 1, 2);
    LocalDate date2 = LocalDate.of(2020, 1, 1);

    LocalDate result = DateHelper.min(date1, date2);
    assertThat(result).isEqualTo(date2);

    result = DateHelper.min(date2, date1);
    assertThat(result).isEqualTo(date2);
  }
}