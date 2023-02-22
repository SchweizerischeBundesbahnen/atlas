package ch.sbb.atlas.timetable.hearing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
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

    TimetableHearingStatement savedStatement = timetableHearingStatementRepository.save(statement);

    assertThat(savedStatement.getId()).isNotNull();
    assertThat(savedStatement.getResponsibleTransportCompanies()).hasSize(2);
    assertThat(savedStatement.getDocuments()).hasSize(3);
  }
}