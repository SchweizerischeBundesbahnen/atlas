package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.ResponsibleTransportCompany;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
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
        .comment("Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die Editierrechte für diese Stellungnahme.")
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
    statement.setComment("Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die Editierrechte für diese Stellungnahme. Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die Editierrechte für diese Stellungnahme.");

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
}