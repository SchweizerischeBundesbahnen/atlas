package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.controller.TimetableHearingStatementControllerInternal;
import ch.sbb.timetable.hearing.controller.TimetableHearingYearControllerInternal;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class TimetableHearingYearClosingTest {

  private static final long YEAR = 2023L;
  private static final long TRANSPORT_COMPANY_ID = 7L;

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearControllerInternal timetableHearingYearController;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal;

  @MockitoBean
  private TransportCompanyClient transportCompanyClient;

  @Autowired
  TimetableHearingYearClosingTest(
      TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearControllerInternal timetableHearingYearController,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearController = timetableHearingYearController;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementControllerInternal = timetableHearingStatementControllerInternal;
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCloseTimetableHearingNotDeletingResponsibleTransportCompanyRelation() {
    TransportCompanyModel transportCompany = TransportCompanyModel.builder()
        .id(TRANSPORT_COMPANY_ID)
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen").build();
    when(transportCompanyClient.getTransportCompany(TRANSPORT_COMPANY_ID)).thenReturn(transportCompany);

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
    TimetableHearingStatementModelV2 junkStatement = timetableHearingStatementControllerInternal.createStatement(statementModel,
        Collections.emptyList());

    // Update to Junk
    statementModel.setStatementStatus(StatementStatus.JUNK);
    junkStatement = timetableHearingStatementControllerInternal.updateHearingStatement(junkStatement.getId(), statementModel,
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
    TimetableHearingStatementModelV2 secondStatement = timetableHearingStatementControllerInternal.createStatement(
        statementModel2,
        Collections.emptyList());

    // when closing
    TimetableHearingYearModel closed = timetableHearingYearController.closeTimetableHearing(YEAR, Collections.emptyList());

    // then
    assertThat(closed.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);

    // Junk got deleted
    Optional<TimetableHearingStatement> junkStatementAfterClose = timetableHearingStatementRepository.findById(
        junkStatement.getId());
    assertThat(junkStatementAfterClose).isEmpty();

    Optional<TimetableHearingStatement> secondStatementAfterClose = timetableHearingStatementRepository.findById(
        secondStatement.getId());
    assertThat(secondStatementAfterClose).isPresent();

    assertThat(secondStatementAfterClose.orElseThrow().getResponsibleTransportCompanyIds())
        .containsExactly(TRANSPORT_COMPANY_ID);
  }
}
