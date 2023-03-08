package ch.sbb.line.directory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableHearingYearTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAcceptValidHearing() {
    // given
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(2023L)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2022, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .version(1)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingYear>> constraintViolations = validator.validate(timetableHearingYear);

    // then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptHearingBefore2010() {
    // given
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(2009L)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2008, 1, 1))
        .hearingTo(LocalDate.of(2008, 2, 1))
        .version(1)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingYear>> constraintViolations = validator.validate(timetableHearingYear);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptHearingFromAfterTo() {
    // given
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(2022L)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2021, 3, 1))
        .hearingTo(LocalDate.of(2021, 2, 1))
        .version(1)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingYear>> constraintViolations = validator.validate(timetableHearingYear);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptHearingWithFromNotTheYearBefore() {
    // given
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(2023L)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2021, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .version(1)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingYear>> constraintViolations = validator.validate(timetableHearingYear);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptHearingWithToNotTheYearBefore() {
    // given
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(2023L)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2021, 1, 1))
        .hearingTo(LocalDate.of(2021, 2, 1))
        .version(1)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingYear>> constraintViolations = validator.validate(timetableHearingYear);

    // then
    assertThat(constraintViolations).hasSize(2);
  }
}