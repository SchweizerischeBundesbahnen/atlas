package ch.sbb.workflow.sepodi.termination.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.workflow.exception.TerminationDateBeforeException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class TerminationStopPointWorkflowServiceTest {

  private static final String SLOID = "ch:sloid:1";
  private static final long VERSION_ID = 1000L;

  @Autowired
  private TerminationStopPointWorkflowService service;

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void shouldNotAddDecisionInfoPlusWhenTerminationDateIsBeforeBoTerminationDate() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = saveTerminationStopPointWorkflow();

    TerminationDecisionModel decisionModel = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .terminationDate(LocalDate.of(1099, 1, 1))
        .build();

    //when and then
    assertThrows(TerminationDateBeforeException.class, () -> service.addDecisionInfoPlus(decisionModel,
        stopPointWorkflow.getId()));
  }

  @Test
  void shouldNotAddDecisionInfoPlusWhenDecisionAlreadyExists() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = saveTerminationStopPointWorkflow();
    TerminationDecision decision = TerminationDecision.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .build();
    stopPointWorkflow.setInfoPlusDecision(decision);
    stopPointWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_APPROVED);
    repository.save(stopPointWorkflow);

    TerminationDecisionModel decisionModel = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .terminationDate(LocalDate.of(2001, 1, 1))
        .build();

    //when and then
    assertThrows(TerminationStopPointWorkflowPreconditionStatusException.class, () -> service.addDecisionInfoPlus(decisionModel,
        stopPointWorkflow.getId()));
  }

  @Test
  void shouldAddYesDecisionInfoPlus() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = saveTerminationStopPointWorkflow();

    TerminationDecisionModel decisionModel = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .terminationDate(LocalDate.of(2001, 1, 1))
        .build();

    //when
    TerminationStopPointWorkflow result = service.addDecisionInfoPlus(decisionModel,
        stopPointWorkflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(2001, 1, 1));

    TerminationDecision infoPlusDecisionResult = result.getInfoPlusDecision();
    assertThat(infoPlusDecisionResult).isNotNull();
    assertThat(infoPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(infoPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.INFO_PLUS);

    verify(notificationService, times(1))
        .sendTariffStopApprovedNotificationToNovaAndBo(any(TerminationStopPointWorkflow.class));
    verify(notificationService, never())
        .sendTariffStopNotApprovedNotificationToBo(any(TerminationStopPointWorkflow.class), any(TerminationDecisionModel.class));
  }

  @Test
  void shouldAddNoDecisionInfoPlus() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = saveTerminationStopPointWorkflow();

    TerminationDecisionModel decisionModel = TerminationDecisionModel.builder()
        .judgement(JudgementType.NO)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .terminationDate(LocalDate.of(2001, 1, 1))
        .build();

    //when
    TerminationStopPointWorkflow result = service.addDecisionInfoPlus(decisionModel,
        stopPointWorkflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_NOT_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(2001, 1, 1));

    TerminationDecision infoPlusDecisionResult = result.getInfoPlusDecision();
    assertThat(infoPlusDecisionResult).isNotNull();
    assertThat(infoPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.NO);
    assertThat(infoPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.INFO_PLUS);

    verify(notificationService, never())
        .sendTariffStopApprovedNotificationToNovaAndBo(any(TerminationStopPointWorkflow.class));
    verify(notificationService, times(1))
        .sendTariffStopNotApprovedNotificationToBo(any(TerminationStopPointWorkflow.class), any(TerminationDecisionModel.class));
  }

  @Test
  void shouldNotStartTerminationWorkflowWhenWorkflowAlreadyExists() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    repository.save(workflow);
    StartTerminationStopPointWorkflowModel stopPointWorkflowModel = buildTerminationStopPointWorkflowModel();
    ReadServicePointVersionModel readServicePointVersionModel = buildReadServicePointVersionModel();
    when(sePoDiAdminClient.startServicePointTermination(stopPointWorkflowModel.getSloid(),
        stopPointWorkflowModel.getVersionId(),
        UpdateTerminationServicePointModel.builder().terminationInProgress(true).build()))
        .thenReturn(readServicePointVersionModel);
    //when and then
    assertThrows(TerminationStopPointWorkflowAlreadyInStatusException.class,
        () -> service.startTerminationWorkflow(stopPointWorkflowModel));
  }

  @Test
  void shouldStartTerminationWorkflow() {
    //given
    StartTerminationStopPointWorkflowModel stopPointWorkflowModel = buildTerminationStopPointWorkflowModel();
    ReadServicePointVersionModel readServicePointVersionModel = buildReadServicePointVersionModel();
    UpdateTerminationServicePointModel terminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(true)
        .terminationDate(stopPointWorkflowModel.getBoTerminationDate())
        .build();
    when(sePoDiAdminClient.startServicePointTermination(
        stopPointWorkflowModel.getSloid(),
        stopPointWorkflowModel.getVersionId(),
        terminationServicePointModel))
        .thenReturn(readServicePointVersionModel);
    //when
    TerminationStopPointWorkflow result = service.startTerminationWorkflow(stopPointWorkflowModel);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);
    verify(notificationService, times(1)).sendStartTerminationNotificationToInfoPlusAndBo(
        any(TerminationStopPointWorkflow.class));
  }

  @Test
  void shouldAbortTerminationWorkflowWhenWorkflowIsStarted() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    stopPointWorkflow.setStatus(TerminationWorkflowStatus.STARTED);
    repository.save(stopPointWorkflow);
    TerminationAbortModel abortingTerminationWorkflow = TerminationAbortModel.builder()
        .abortComment("Aborting termination workflow").build();
    //when
    TerminationStopPointWorkflow result = service.abortTerminationWorkflow(stopPointWorkflow.getId(),
        abortingTerminationWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.CANCELED);
    verify(notificationService, times(1)).sendAbortNotificationToBoAndInfoPlus(any(TerminationStopPointWorkflow.class));
  }

  @Test
  void shouldAbortTerminationWorkflowWhenWorkflowIsTariffStopApproved() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    stopPointWorkflow.setStatus(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    repository.save(stopPointWorkflow);
    TerminationAbortModel abortingTerminationWorkflow = TerminationAbortModel.builder()
        .abortComment("Aborting termination workflow").build();
    //when
    TerminationStopPointWorkflow result = service.abortTerminationWorkflow(stopPointWorkflow.getId(),
        abortingTerminationWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.CANCELED);
    verify(notificationService, times(1)).sendAbortNotificationToBoInfoPlusAndNova(any(TerminationStopPointWorkflow.class));
  }

  @Test
  void shouldAbortTerminationWorkflowWhenWorkflowIsTerminationNotApproved() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    stopPointWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
    repository.save(stopPointWorkflow);
    TerminationAbortModel abortingTerminationWorkflow = TerminationAbortModel.builder()
        .abortComment("Aborting termination workflow").build();
    //when
    TerminationStopPointWorkflow result = service.abortTerminationWorkflow(stopPointWorkflow.getId(),
        abortingTerminationWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED_CLOSED);
    verify(notificationService, never()).sendAbortNotificationToBoAndInfoPlus(any(TerminationStopPointWorkflow.class));
    verify(notificationService, never()).sendAbortNotificationToBoInfoPlusAndNova(any(TerminationStopPointWorkflow.class));
  }

  private @NotNull TerminationStopPointWorkflow saveTerminationStopPointWorkflow() {
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    return repository.save(workflow);
  }

  private static StartTerminationStopPointWorkflowModel buildTerminationStopPointWorkflowModel() {
    return StartTerminationStopPointWorkflowModel.builder()
        .sloid(SLOID)
        .versionId(VERSION_ID)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .applicantMail("a@b.com")
        .build();
  }

  private static ReadServicePointVersionModel buildReadServicePointVersionModel() {
    return ReadServicePointVersionModel
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500030))
        .sloid("ch:1:sloid:30")
        .designationLong(null)
        .designationOfficial("Basel EuroAirport P")
        .abbreviation("BSEU")
        .meansOfTransport(Collections.emptyList())
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .validFrom(LocalDate.of(2000, 2, 7))
        .validTo(LocalDate.of(2007, 2, 26))
        .categories(List.of(Category.MAINTENANCE_POINT, Category.HOSTNAME, Category.MIGRATION_DIVERSE))
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.UNKNOWN)
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .editor("fs45117")
        .build();

  }

  private static TerminationStopPointWorkflow getStopPointWorkflow() {
    return TerminationStopPointWorkflow.builder()
        .sloid(SLOID)
        .versionId(VERSION_ID)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .infoPlusDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).build())
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .novaDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.NOVA).build())
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .versionValidTo(LocalDate.of(2099, 12, 31))
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
  }

}