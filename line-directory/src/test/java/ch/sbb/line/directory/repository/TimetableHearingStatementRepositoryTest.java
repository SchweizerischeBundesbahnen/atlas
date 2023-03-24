package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.ResponsibleTransportCompany;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement savedStatement = timetableHearingStatementRepository.save(statement);

    assertThat(savedStatement.getId()).isNotNull();
  }
}