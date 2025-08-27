package ch.sbb.workflow.module.sepodi.termination.service;

import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TARIFF_STOP_APPROVED;
import static ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus.TERMINATION_NOT_APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.workflow.aop.LoggingAspect;
import ch.sbb.workflow.exception.SePoDiClientException;
import ch.sbb.workflow.module.sepodi.termination.exception.TerminationDateBeforeException;
import ch.sbb.workflow.module.sepodi.termination.exception.TerminationStopPointWorkflowAlreadyInStatusException;
import ch.sbb.workflow.module.sepodi.termination.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.module.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.module.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.module.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
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

  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  public void setUp() {
    listAppender = new ListAppender<>();
    Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
    listAppender.stop();
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
    assertThat(result.getStatus()).isEqualTo(TARIFF_STOP_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(2001, 1, 1));

    TerminationDecision infoPlusDecisionResult = result.getInfoPlusDecision();
    assertThat(infoPlusDecisionResult).isNotNull();
    assertThat(infoPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(infoPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.INFO_PLUS);

    verify(notificationService, times(1))
        .sendTariffStopApprovedNotificationToNova(any(TerminationStopPointWorkflow.class));
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
        .sendTariffStopApprovedNotificationToNova(any(TerminationStopPointWorkflow.class));
    verify(notificationService, times(1))
        .sendTariffStopNotApprovedNotificationToBo(any(TerminationStopPointWorkflow.class), any(TerminationDecisionModel.class));
  }

  @Test
  void shouldAddDecisionNovaAccepted() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    workflow.setInfoPlusTerminationDate(LocalDate.of(8000, 1, 1));
    workflow.setVersionValidTo(LocalDate.of(9999, 12, 31));
    workflow.setStatus(TARIFF_STOP_APPROVED);
    repository.save(workflow);

    TerminationDecisionModel novaDecision = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .terminationDate(LocalDate.of(8001, 1, 1))
        .build();
    //when
    TerminationStopPointWorkflow result = service.addDecisionNova(novaDecision, workflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(8000, 1, 1));
    assertThat(result.getNovaTerminationDate()).isEqualTo(LocalDate.of(8001, 1, 1));

    TerminationDecision novaPlusDecisionResult = result.getNovaDecision();
    assertThat(novaPlusDecisionResult).isNotNull();
    assertThat(novaPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(novaPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.NOVA);

    verify(notificationService, times(1))
        .sendTerminationConfirmedNotification(any(TerminationStopPointWorkflow.class));
  }

  @Test
  void shouldAddDecisionNovaLoggingAspect() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    workflow.setInfoPlusTerminationDate(LocalDate.of(8000, 1, 1));
    workflow.setVersionValidTo(LocalDate.of(9999, 12, 31));
    workflow.setStatus(TARIFF_STOP_APPROVED);
    repository.save(workflow);

    TerminationDecisionModel novaDecision = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .terminationDate(LocalDate.of(8001, 1, 1))
        .build();

    ErrorResponse errorResponse = ErrorResponse.builder().details(new TreeSet<>()).build();
    SePoDiClientException sePoDiClientException = new SePoDiClientException(errorResponse);
    doThrow(sePoDiClientException).when(sePoDiAdminClient).terminateStopPoint(any());
    //when
    assertThrows(SePoDiClientException.class,
        () -> service.addDecisionNova(novaDecision, workflow.getId()));

    //then
    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage()
                .contains("\"workflowType\":" + "\"" + LoggingAspect.ADD_TERMINATION_DECISION_NOVA + "\""));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldAddDecisionNovaAcceptedWithoutTermination() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    workflow.setInfoPlusTerminationDate(LocalDate.of(8000, 1, 1));
    workflow.setVersionValidTo(LocalDate.of(9999, 12, 31));
    workflow.setStatus(TARIFF_STOP_APPROVED);
    repository.save(workflow);

    TerminationDecisionModel novaDecision = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .terminationDate(LocalDate.of(9999, 12, 31))
        .build();
    //when
    TerminationStopPointWorkflow result = service.addDecisionNova(novaDecision, workflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(8000, 1, 1));
    assertThat(result.getNovaTerminationDate()).isEqualTo(LocalDate.of(9999, 12, 31));

    TerminationDecision novaPlusDecisionResult = result.getNovaDecision();
    assertThat(novaPlusDecisionResult).isNotNull();
    assertThat(novaPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(novaPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.NOVA);

    verify(notificationService, times(1))
        .sendTerminationConfirmedNotification(any(TerminationStopPointWorkflow.class));
    verify(sePoDiAdminClient, never())
        .terminateStopPoint(any(StopPointWorkflowTerminationModel.class));
  }

  @Test
  void shouldAddDecisionNovaNotAccepted() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    workflow.setInfoPlusTerminationDate(LocalDate.of(8000, 1, 1));
    workflow.setVersionValidTo(LocalDate.of(9999, 12, 31));
    workflow.setStatus(TARIFF_STOP_APPROVED);
    repository.save(workflow);

    TerminationDecisionModel novaDecision = TerminationDecisionModel.builder()
        .judgement(JudgementType.NO)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .terminationDate(LocalDate.of(8001, 1, 1))
        .build();
    //when
    TerminationStopPointWorkflow result = service.addDecisionNova(novaDecision, workflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(8000, 1, 1));
    assertThat(result.getNovaTerminationDate()).isEqualTo(LocalDate.of(8001, 1, 1));

    TerminationDecision novaPlusDecisionResult = result.getNovaDecision();
    assertThat(novaPlusDecisionResult).isNotNull();
    assertThat(novaPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.NO);
    assertThat(novaPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.NOVA);

    verify(notificationService, times(1))
        .sendTerminationConfirmedNotification(any(TerminationStopPointWorkflow.class));
  }

  @Test
  void shouldAddDecisionNovaAndNotSendNotificationWhenAlreadyInStatusTerminationNotApproved() {
    //given
    TerminationStopPointWorkflow workflow = getStopPointWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    workflow.setInfoPlusTerminationDate(LocalDate.of(8000, 1, 1));
    workflow.setVersionValidTo(LocalDate.of(9999, 12, 31));
    workflow.setStatus(TERMINATION_NOT_APPROVED);
    repository.save(workflow);

    TerminationDecisionModel novaDecision = TerminationDecisionModel.builder()
        .judgement(JudgementType.YES)
        .motivation("Forza Napoli")
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .terminationDate(LocalDate.of(8001, 1, 1))
        .build();
    //when
    TerminationStopPointWorkflow result = service.addDecisionNova(novaDecision, workflow.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
    assertThat(result.getBoTerminationDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getInfoPlusTerminationDate()).isEqualTo(LocalDate.of(8000, 1, 1));
    assertThat(result.getNovaTerminationDate()).isEqualTo(LocalDate.of(8001, 1, 1));

    TerminationDecision novaPlusDecisionResult = result.getNovaDecision();
    assertThat(novaPlusDecisionResult).isNotNull();
    assertThat(novaPlusDecisionResult.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(novaPlusDecisionResult.getTerminationDecisionPerson()).isEqualTo(TerminationDecisionPerson.NOVA);

    verify(notificationService, never())
        .sendTerminationConfirmedNotification(any(TerminationStopPointWorkflow.class));
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
  void shouldStartTerminationWorkflowLoggingCorrectly() {
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

    ErrorResponse errorResponse = ErrorResponse.builder().details(new TreeSet<>()).build();
    SePoDiClientException sePoDiClientException = new SePoDiClientException(errorResponse);
    doThrow(sePoDiClientException).when(sePoDiAdminClient).startServicePointTermination(any(), any(), any());
    //when
    assertThrows(SePoDiClientException.class,
        () -> service.startTerminationWorkflow(stopPointWorkflowModel));

    //then
    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage()
                .contains("\"workflowType\":" + "\"" + LoggingAspect.START_TERMINATION_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
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
    verify(notificationService, times(1)).sendAbortNotificationToBoAndInfoPlus(any(TerminationStopPointWorkflow.class),
        any(TerminationAbortModel.class));
  }

  @Test
  void shouldAbortTerminationWorkflowWhenWorkflowIsTariffStopApproved() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    stopPointWorkflow.setStatus(TARIFF_STOP_APPROVED);
    repository.save(stopPointWorkflow);
    TerminationAbortModel abortingTerminationWorkflow = TerminationAbortModel.builder()
        .abortComment("Aborting termination workflow").build();
    //when
    TerminationStopPointWorkflow result = service.abortTerminationWorkflow(stopPointWorkflow.getId(),
        abortingTerminationWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.CANCELED);
    verify(notificationService, times(1)).sendAbortNotificationToBoInfoPlusAndNova(any(TerminationStopPointWorkflow.class),
        any(TerminationAbortModel.class));
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
    verify(notificationService, never()).sendAbortNotificationToBoAndInfoPlus(any(TerminationStopPointWorkflow.class),
        any(TerminationAbortModel.class));
    verify(notificationService, never()).sendAbortNotificationToBoInfoPlusAndNova(any(TerminationStopPointWorkflow.class),
        any(TerminationAbortModel.class));
  }

  @Test
  void shouldAbortTerminationWorkflowWhenWorkflowLoggingCorrectly() {
    //given
    TerminationStopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    stopPointWorkflow.setStatus(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
    repository.save(stopPointWorkflow);
    TerminationAbortModel abortingTerminationWorkflow = TerminationAbortModel.builder()
        .abortComment("Aborting termination workflow").build();

    ErrorResponse errorResponse = ErrorResponse.builder().details(new TreeSet<>()).build();
    SePoDiClientException sePoDiClientException = new SePoDiClientException(errorResponse);
    doThrow(sePoDiClientException).when(sePoDiAdminClient).stopServicePointTermination(any(), any());
    //when
    assertThrows(SePoDiClientException.class,
        () -> service.abortTerminationWorkflow(stopPointWorkflow.getId(), abortingTerminationWorkflow));

    //then
    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage()
                .contains("\"workflowType\":" + "\"" + LoggingAspect.ABORT_TERMINATION_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
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