package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.ResponsibleTransportCompany;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

@IntegrationTest
public class TimetableHearingStatementRepositoryTest {

  private final TimetableHearingStatementRepository timetableHearingStatementRepository;

  @Autowired
  public TimetableHearingStatementRepositoryTest(TimetableHearingStatementRepository timetableHearingStatementRepository) {
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
  }

  private static TimetableHearingStatement getMinimalTimetableHearingStatement() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    return statement;
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
  }

  @Test
  void shouldCreateNewHearingStatement() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .ttfnid("ch:1:ttfnid:1235234")
        .swissCanton(SwissCanton.BERN)
        .stopPlace("Erste Haltestelle ... weisst ja")
        .statementSender(StatementSender.builder()
            .firstName("Mike")
            .lastName("von Bike")
            .organisation("Bewerber")
            .street("Hauptstrasse 1")
            .zip(39012)
            .city("Algund")
            .email("mike@thebike.com")
            .build())
        .comment(
            "Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die "
                + "Editierrechte für diese Stellungnahme.")
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Weil ich mag")
        .build();
    statement.setDocuments(Set.of(StatementDocument.builder()
            .statement(statement)
            .fileName("doc1")
            .fileSize(6454L)
            .build(),
        StatementDocument.builder()
            .statement(statement)
            .fileName("doc2")
            .fileSize(2454L)
            .build(),
        StatementDocument.builder()
            .statement(statement)
            .fileName("doc3")
            .fileSize(3454L)
            .build()));
    statement.setResponsibleTransportCompanies(Set.of(ResponsibleTransportCompany.builder()
        .statement(statement)
        .transportCompanyId(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen")
        .build()));

    TimetableHearingStatement savedStatement = timetableHearingStatementRepository.save(statement);

    assertThat(savedStatement.getId()).isNotNull();
    assertThat(savedStatement.getResponsibleTransportCompanies()).hasSize(1);
    assertThat(savedStatement.getDocuments()).hasSize(3);
  }

  @Test
  void shouldCreateMinimalHearingStatement() {
    TimetableHearingStatement statement = getMinimalTimetableHearingStatement();

    TimetableHearingStatement savedStatement = timetableHearingStatementRepository.save(statement);

    assertThat(savedStatement.getId()).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenCommentLengthIsGreaterThan280Characters() {
    TimetableHearingStatement statement = getMinimalTimetableHearingStatement();
    statement.setComment(
        "Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die Editierrechte "
            + "für diese Stellungnahme. Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung "
            + "verlieren Sie die Editierrechte für diese Stellungnahme.");

    assertThrows(TransactionSystemException.class,
        () -> timetableHearingStatementRepository.save(statement));
  }

  @Test
  void shouldThrowExceptionWhenStopPlaceLengthIsGreaterThan50Characters() {
    TimetableHearingStatement statement = getMinimalTimetableHearingStatement();
    statement.setStopPlace("Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme.");

    assertThrows(TransactionSystemException.class,
        () -> timetableHearingStatementRepository.save(statement));
  }

  @Test
  void shouldUpdateHearingStatementStatusWithJustification() {
    //given
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Hopp YB")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement);

    //when
    timetableHearingStatementRepository.updateHearingStatementStatusWithJustification(
        statement.getId(), StatementStatus.ACCEPTED, "Napoli ist besser als YB");

    //then
    Optional<TimetableHearingStatement> result = timetableHearingStatementRepository.findById(statement.getId());
    assertThat(result).isNotNull();
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);
    assertThat(result.get().getJustification()).isEqualTo("Napoli ist besser als YB");
  }

  @Test
  void shouldUpdateHearingStatementStatus() {
    //given
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Forza Napoli")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement);

    //when
    timetableHearingStatementRepository.updateHearingStatementStatus(statement.getId(), StatementStatus.ACCEPTED);

    //then
    Optional<TimetableHearingStatement> result = timetableHearingStatementRepository.findById(statement.getId());
    assertThat(result).isNotNull();
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);
    assertThat(result.get().getJustification()).isEqualTo("Forza Napoli");
  }

  @Test
  void shouldUpdateHearingCantonWithComment() {
    //given
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Forza Napoli")
        .comment("Just a comment")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement);

    //when
    timetableHearingStatementRepository.updateHearingCantonWithComment(statement.getId(), SwissCanton.AARGAU, "Not just a "
        + "comment");

    //then
    Optional<TimetableHearingStatement> result = timetableHearingStatementRepository.findById(statement.getId());
    assertThat(result).isNotNull();
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(result.get().getSwissCanton()).isEqualTo(SwissCanton.AARGAU);
    assertThat(result.get().getComment()).isEqualTo("Not just a comment");
  }

  @Test
  void shouldUpdateHearingCanton() {
    //given
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .justification("Forza Napoli")
        .comment("Just a comment")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement);

    //when
    timetableHearingStatementRepository.updateHearingCanton(statement.getId(), SwissCanton.AARGAU);

    //then
    Optional<TimetableHearingStatement> result = timetableHearingStatementRepository.findById(statement.getId());
    assertThat(result).isNotNull();
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(result.get().getSwissCanton()).isEqualTo(SwissCanton.AARGAU);
    assertThat(result.get().getComment()).isEqualTo("Just a comment");
  }

  @Test
  void shouldDeleteOnlyByStatusAndYear() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2022L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement3 = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    timetableHearingStatementRepository.save(statement);
    timetableHearingStatementRepository.save(statement2);
    timetableHearingStatementRepository.save(statement3);

    timetableHearingStatementRepository.deleteByStatementStatusAndTimetableYear(StatementStatus.RECEIVED, 2023L);

    List<TimetableHearingStatement> result = timetableHearingStatementRepository.findAll();
    assertThat(result).hasSize(2);

    List<TimetableHearingStatement> filterDeletedStatements = result.stream().filter(
            resultStatement -> resultStatement.getTimetableYear() == 2023L
                && resultStatement.getStatementStatus() == StatementStatus.RECEIVED)
        .toList();

    assertThat(filterDeletedStatements).hasSize(0);
  }

  @Test
  void shouldFindAllByStatementStatusInAndTimetableYear() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2022L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement3 = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    timetableHearingStatementRepository.save(statement);
    timetableHearingStatementRepository.save(statement2);
    timetableHearingStatementRepository.save(statement3);

    List<TimetableHearingStatement> result = timetableHearingStatementRepository.findAllByStatementStatusInAndTimetableYear(
        List.of(StatementStatus.RECEIVED,
            StatementStatus.JUNK), 2023L);

    assertThat(result).hasSize(2);
    assertTrue(result.stream().noneMatch(resultStatement -> Objects.equals(resultStatement.getTimetableYear(),
        statement2.getTimetableYear())));
  }

}
