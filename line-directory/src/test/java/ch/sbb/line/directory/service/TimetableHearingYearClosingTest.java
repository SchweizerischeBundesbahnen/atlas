package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.controller.TimetableHearingStatementControllerV2;
import ch.sbb.line.directory.controller.TimetableHearingYearController;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TimetableHearingYearClosingTest {

  private static final long YEAR = 2023L;
  private static final long TRANSPORT_COMPANY_ID = 7L;

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;
  @Autowired
  private TimetableHearingYearController timetableHearingYearController;

  @Autowired
  private TimetableHearingStatementRepository timetableHearingStatementRepository;
  @Autowired
  private TimetableHearingStatementControllerV2 timetableHearingStatementControllerV2;

  @Autowired
  private SharedTransportCompanyRepository sharedTransportCompanyRepository;

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
    sharedTransportCompanyRepository.deleteAll();
  }

  @Test
  void shouldCloseTimetableHearingNotDeletingSharedTransportCompany() {
    // given TransportCompany
    sharedTransportCompanyRepository.save(SharedTransportCompanyModel.builder()
        .id(TRANSPORT_COMPANY_ID)
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen").build());
    sharedTransportCompanyRepository.flush();

    // given Year
    timetableHearingYearController.createHearingYear(TimetableHearingYearModel.builder()
        .timetableYear(YEAR)
        .hearingFrom(LocalDate.of(2022, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .build());
    timetableHearingYearController.startHearingYear(YEAR);

    // given Statement 1
    TimetableHearingStatementModelV2 statementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .responsibleTransportCompanies(List.of(
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .id(TRANSPORT_COMPANY_ID)
                .build()))
        .build();
    TimetableHearingStatementModelV2 junkStatement = timetableHearingStatementControllerV2.createStatement(statementModel,
        Collections.emptyList());

    // Update to Junk
    statementModel.setStatementStatus(StatementStatus.JUNK);
    junkStatement = timetableHearingStatementControllerV2.updateHearingStatement(junkStatement.getId(), statementModel,
        Collections.emptyList());
    assertThat(junkStatement).isNotNull();
    assertThat(junkStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
    assertThat(junkStatement.getResponsibleTransportCompanies()).hasSize(1);

    // given Statement 2 with same TransportCompany
    TimetableHearingStatementModelV2 statementModel2 = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Morgen.")
        .responsibleTransportCompanies(List.of(
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .id(TRANSPORT_COMPANY_ID)
                .build()))
        .build();
    TimetableHearingStatementModelV2 secondStatement = timetableHearingStatementControllerV2.createStatement(statementModel2,
        Collections.emptyList());

    // when closing
    TimetableHearingYearModel closed = timetableHearingYearController.closeTimetableHearing(YEAR);

    // then
    assertThat(closed.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);

    // Junk got deleted
    Optional<TimetableHearingStatement> junkStatementAfterClose = timetableHearingStatementRepository.findById(
        junkStatement.getId());
    assertThat(junkStatementAfterClose).isEmpty();

    Optional<TimetableHearingStatement> secondStatementAfterClose = timetableHearingStatementRepository.findById(
        secondStatement.getId());
    assertThat(secondStatementAfterClose).isPresent();

    // TransportCompanies still here
    assertThat(sharedTransportCompanyRepository.count()).isEqualTo(1);
  }
}
