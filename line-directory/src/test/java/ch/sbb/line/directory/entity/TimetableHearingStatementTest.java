package ch.sbb.line.directory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.line.directory.entity.TimetableHearingStatement.TimetableHearingStatementBuilder;
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
        .responsibleTransportCompanies(Set.of(SharedTransportCompany.builder()
            .id(1L)
            .number("#0001")
            .abbreviation("SBB")
            .businessRegisterName("Schweizerische Bundesbahnen")
            .build()))
        .statementSender(StatementSender.builder()
            .firstName("Mike")
            .lastName("von Bike")
            .organisation("Bewerber")
            .street("Hauptstrasse 1")
            .zip(39012)
            .city("Algund")
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Weil ich mag")
        .documents(Set.of(StatementDocument.builder()
                .fileName("doc1")
                .fileSize(6454L)
                .build(),
            StatementDocument.builder()
                .fileName("doc2")
                .fileSize(2454L)
                .build(),
            StatementDocument.builder()
                .fileName("doc3")
                .fileSize(3454L)
                .build()))
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
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .zip(999)
            .build())
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
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .zip(100000)
            .build())
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
        .documents(Set.of(StatementDocument.builder()
                .fileName("doc1")
                .fileSize(6454L)
                .build(),
            StatementDocument.builder()
                .fileName("doc2")
                .fileSize(2454L)
                .build(),
            StatementDocument.builder()
                .fileName("doc3")
                .fileSize(3454L)
                .build(),
            StatementDocument.builder()
                .fileName("doc4")
                .fileSize(4454L)
                .build()))
        .build();

    //when
    Set<ConstraintViolation<TimetableHearingStatement>> constraintViolations = validator.validate(statement);

    // then
    assertThat(constraintViolations).hasSize(1);
  }

  private TimetableHearingStatementBuilder<?, ?> minimalStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .version(1);
  }

}