package ch.sbb.atlas.versioning.helper;

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
}
