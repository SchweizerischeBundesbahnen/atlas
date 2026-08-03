package ch.sbb.timetable.hearing.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.entity.StatementDocument;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.shared.transportcompany.entity.SharedTransportCompany;
import ch.sbb.timetable.hearing.shared.transportcompany.repository.SharedTransportCompanyRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

@IntegrationTest
class TimetableHearingStatementRepositoryTest {

  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;

  @Autowired
  TimetableHearingStatementRepositoryTest(
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      SharedTransportCompanyRepository sharedTransportCompanyRepository) {
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.sharedTransportCompanyRepository = sharedTransportCompanyRepository;
  }

  private static TimetableHearingStatement getMinimalTimetableHearingStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    sharedTransportCompanyRepository.deleteAll();
  }

  @Test
  void shouldCreateNewHearingStatement() {
    sharedTransportCompanyRepository.save(SharedTransportCompany.builder()
        .id(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .build());

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
            .emails(List.of("mike@thebike.com"))
            .build())
        .cantonTransferComment(
            "Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die "
                + "Editierrechte für diese Stellungnahme.")
        .statement("Ich mag bitte mehr Bös fahren")
        .publicComment("Weil ich mag")
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
    statement.setResponsibleTransportCompanies(Set.of(SharedTransportCompany.builder()
        .id(1L)
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
    statement.setCantonTransferComment(
        "Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung verlieren Sie die Editierrechte "
            + "für diese Stellungnahme. Sie ändern die Kantonszuordnung der ausgewählten Stellungnahme. Mit der Übertragung "
            + "verlieren Sie die Editierrechte für diese Stellungnahme.");

    assertThrows(TransactionSystemException.class,
        () -> timetableHearingStatementRepository.save(statement));
  }

  @Test
  void shouldThrowExceptionWhenStopPlaceLengthIsGreaterThan255Characters() {
    TimetableHearingStatement statement = getMinimalTimetableHearingStatement();
    String stopPlace = """
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        Ich möchte da drüben bitte auch abfahren. Weisst du?
        """;
    statement.setStopPlace(stopPlace);

    assertThrows(TransactionSystemException.class,
        () -> timetableHearingStatementRepository.save(statement));
  }

  @Test
  void shouldDeleteOnlyByStatusAndYear() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2022L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement3 = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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

    assertThat(filterDeletedStatements).isEmpty();
  }

  @Test
  void shouldFindAllByStatementStatusInAndTimetableYear() {
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2022L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement3 = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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

  @Test
  void shouldRemoveDossierRelationAndSetReceivedForSpecificStatements() {
    // given
    TimetableHearingStatement statementOne = getMinimalTimetableHearingStatement();
    statementOne.setDossierId(1L);
    statementOne.setDossierContactMail("test@atlas.ch");
    statementOne.setStatementStatus(StatementStatus.ACCEPTED);

    TimetableHearingStatement statementTwo = getMinimalTimetableHearingStatement();
    statementTwo.setDossierId(2L);
    statementTwo.setDossierContactMail("test@atlas.ch");
    statementTwo.setStatementStatus(StatementStatus.IN_REVIEW);

    long firstId = timetableHearingStatementRepository.save(statementOne).getId();
    long secondId = timetableHearingStatementRepository.save(statementTwo).getId();
    // when
    timetableHearingStatementRepository.removeDossierRelationAndSetReceivedFor(List.of(firstId, secondId));
    // then
    statementOne = timetableHearingStatementRepository.findById(firstId).get();
    assertThat(statementOne.getDossierId()).isNull();
    assertThat(statementOne.getDossierContactMail()).isNull();
    assertThat(statementOne.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);

    statementTwo = timetableHearingStatementRepository.findById(secondId).get();
    assertThat(statementTwo.getDossierId()).isNull();
    assertThat(statementTwo.getDossierContactMail()).isNull();
    assertThat(statementTwo.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }
}