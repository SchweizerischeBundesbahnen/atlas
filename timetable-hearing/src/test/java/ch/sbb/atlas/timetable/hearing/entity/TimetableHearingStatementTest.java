package ch.sbb.atlas.timetable.hearing.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.SwissCanton;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement.TimetableHearingStatementBuilder;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableHearingStatementTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAcceptValidStatement() {
    // given
    TimetableHearingStatement statement = minimalStatement()
        .ttfnid("ch:1:ttfnid:1235234")
        .swissCanton(SwissCanton.BERN)
        .stopPlace("Erste Haltestelle ... weisst ja")
        .responsibleTransportCompanies(Set.of("#0001", "#0002"))
        .firstName("Mike")
        .lastName("von Bike")
        .organisation("Bewerber")
        .street("Hauptstrasse 1")
        .zip(39012)
        .city("Algund")
        .email("mike@thebike.com")
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Weil ich mag")
        .documents(Set.of("c40f78d3-c7a5-4c3f-bad9-7002ce901dd7", "d8aa292c-4791-4a5d-8d35-314d2390be3c",
            "ba9ff578-1c61-401a-9e13-3906150ae788"))
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldAcceptMinimalStatement() {
    // given
    TimetableHearingStatement statement = minimalStatement().build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptZipCodesWithThreeDigits() {
    // given
    TimetableHearingStatement statement = minimalStatement()
        .zip(999)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptZipCodesWithSixDigits() {
    // given
    TimetableHearingStatement statement = minimalStatement()
        .zip(100000)
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAcceptMoreThanThreeDocuments() {
    // given
    TimetableHearingStatement statement = minimalStatement()
        .documents(Set.of("c40f78d3-c7a5-4c3f-bad9-7002ce901dd7", "d8aa292c-4791-4a5d-8d35-314d2390be3c",
            "ba9ff578-1c61-401a-9e13-3906150ae788", "ba9ff578-1c61-401a-9e13-3906150ae780"))
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  private TimetableHearingStatementBuilder<?, ?> minimalStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .email("mike@thebike.com")
        .statement("Ich mag bitte mehr Bös fahren")
        .version(1);
  }

}