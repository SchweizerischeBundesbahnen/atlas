package ch.sbb.timetable.field.number.versioning.date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateHelperTest {

  @Test
  public void shouldReturnTrueWhenTwoDatesAreSequential(){
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);
    LocalDate next = LocalDate.of(2000, 1, 2);

    //when
    boolean result = DateHelper.areDatesSequential(current, next);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenTwoDatesAreNotSequential(){
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);
    LocalDate next = LocalDate.of(2000, 1, 3);

    //when
    boolean result = DateHelper.areDatesSequential(current, next);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenCurrentDateIsNull(){
    //given
    LocalDate next = LocalDate.of(2000, 1, 3);

    //when
    assertThatThrownBy(() -> {
      DateHelper.areDatesSequential(null, next);
      //then
    }).isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
          "Current date is null");

  }
  @Test
  public void shouldThrowIllegalStateExceptionWhenNextDateIsNull(){
    //given
    LocalDate current = LocalDate.of(2000, 1, 1);

    //when
    assertThatThrownBy(() -> {
      DateHelper.areDatesSequential(current, null);
      //then
    }).isInstanceOf(IllegalStateException.class)
      .hasMessageContaining(
          "Next date is null");

  }

}