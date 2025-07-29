package ch.sbb.workflow.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.security.service.ServicePointTerminationBasedUserAdministrationService;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import ch.sbb.workflow.exception.TerminationStopPointWorkflowPreconditionStatusException;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointNotificationService;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointWorkflowService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Workflow Status Flow according to
 * <a
 * href="https://confluence.sbb.ch/spaces/ATLAS/pages/3057021043/ADR-0031+ServicePoint+Workflow+Haltestellen
 * +Terminierungsworkflow#ADR0031:ServicePointWorkflow(HaltestellenTerminierungsworkflow)-UseCasesDiagramm">UseCases</a>
 */
class TerminationStopPointWorkflowInternalControllerVotingTest extends BaseControllerApiTest {

  private static final StartTerminationStopPointWorkflowModel WORKFLOW = StartTerminationStopPointWorkflowModel.builder()
      .workflowComment("Please terminate this stop point")
      .applicantMail("user@tu.ch")
      .boTerminationDate(LocalDate.of(2020, 1, 1))
      .sloid("ch:1:sboid:132")
      .versionId(123L)
      .build();
  private static final LocalDate TERMINATION_DATE = LocalDate.of(2020, 1, 2);

  @Autowired
  private TerminationStopPointWorkflowInternalController controller;

  @Autowired
  private TerminationStopPointWorkflowService service;

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @Autowired
  private TerminationStopPointFeatureTogglingService featureTogglingService;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @MockitoBean
  private ServicePointTerminationBasedUserAdministrationService servicePointTerminationBasedUserAdministrationService;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @BeforeEach
  void setUp() {
    when(servicePointTerminationBasedUserAdministrationService.hasUserInfoPlusTerminationVotePermission()).thenReturn(true);
    when(servicePointTerminationBasedUserAdministrationService.hasUserNovaTerminationVotePermission()).thenReturn(true);

    ReadServicePointVersionModel servicePointVersionModel = ReadServicePointVersionModel.builder()
        .designationOfficial("official")
        .businessOrganisation("ch:1:sboid:132")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .build();

    when(sePoDiAdminClient.startServicePointTermination(any(), any(), any(
        UpdateTerminationServicePointModel.class))).thenReturn(servicePointVersionModel);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void shouldFlowThroughInfoPlusAndNovaApproval() {
    TerminationStopPointWorkflowModel terminationWorkflow = controller.startTerminationStopPointWorkflow(
        WORKFLOW);
    assertThat(terminationWorkflow.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);
    verify(notificationService).sendStartTerminationNotificationToInfoPlusAndBo(any());

    LocalDate infoPlusTerminationDate = TERMINATION_DATE;
    TerminationStopPointWorkflowModel infoPlusApprovedTermination = controller.decisionInfoPlus(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
            .terminationDate(infoPlusTerminationDate)
            .build(), terminationWorkflow.getId());
    assertThat(infoPlusApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    verify(notificationService).sendTariffStopApprovedNotificationToNovaAndBo(any());

    LocalDate novaTerminationDate = TERMINATION_DATE.plusMonths(1);
    TerminationStopPointWorkflowModel novaApprovedTermination = controller.decisionNova(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
            .terminationDate(novaTerminationDate)
            .build(), terminationWorkflow.getId());
    assertThat(novaApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
    verify(sePoDiAdminClient, never()).changeToTariffStop(eq(StopPointWorkflowTerminationModel.builder()
        .sloid(terminationWorkflow.getSloid())
        .versionId(terminationWorkflow.getVersionId())
        .terminationDate(infoPlusTerminationDate)
        .build()));
    verify(sePoDiAdminClient).terminateStopPoint(eq(StopPointWorkflowTerminationModel.builder()
        .sloid(terminationWorkflow.getSloid())
        .versionId(terminationWorkflow.getVersionId())
        .terminationDate(novaTerminationDate)
        .build()));
  }

  @Test
  void shouldFlowThroughInfoPlusAndNovaApprovalOnLaterNovaDate() {
    TerminationStopPointWorkflowModel terminationWorkflow = controller.startTerminationStopPointWorkflow(
        WORKFLOW);
    assertThat(terminationWorkflow.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);
    verify(notificationService).sendStartTerminationNotificationToInfoPlusAndBo(any());

    TerminationStopPointWorkflowModel infoPlusApprovedTermination = controller.decisionInfoPlus(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
            .terminationDate(TERMINATION_DATE)
            .build(), terminationWorkflow.getId());
    assertThat(infoPlusApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);
    verify(notificationService).sendTariffStopApprovedNotificationToNovaAndBo(any());

    TerminationStopPointWorkflowModel novaApprovedTermination = controller.decisionNova(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
            .terminationDate(TERMINATION_DATE.plusMonths(1))
            .build(), terminationWorkflow.getId());
    assertThat(novaApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
    verify(sePoDiAdminClient).terminateStopPoint(any());
    verify(sePoDiAdminClient).changeToTariffStop(any());
  }

  @Test
  void shouldFlowThroughInfoPlusDisapproval() {
    TerminationStopPointWorkflowModel terminationWorkflow = controller.startTerminationStopPointWorkflow(
        WORKFLOW);
    assertThat(terminationWorkflow.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);
    verify(notificationService).sendStartTerminationNotificationToInfoPlusAndBo(any());

    TerminationStopPointWorkflowModel infoPlusApprovedTermination = controller.decisionInfoPlus(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.NO)
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
            .terminationDate(TERMINATION_DATE)
            .build(), terminationWorkflow.getId());
    assertThat(infoPlusApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_NOT_APPROVED);
    verify(notificationService).sendTariffStopNotApprovedNotificationToBo(any(), any());
    verify(sePoDiAdminClient).stopServicePointTermination(any(), any());

    assertThatExceptionOfType(TerminationStopPointWorkflowPreconditionStatusException.class).isThrownBy(
        () -> controller.decisionNova(
            TerminationDecisionModel.builder()
                .judgement(JudgementType.NO)
                .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
                .terminationDate(TERMINATION_DATE)
                .build(), terminationWorkflow.getId()));
  }

  @Test
  void shouldFlowThroughInfoPlusApprovalButNovaDisapproval() {
    TerminationStopPointWorkflowModel terminationWorkflow = controller.startTerminationStopPointWorkflow(
        WORKFLOW);
    assertThat(terminationWorkflow.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);

    TerminationStopPointWorkflowModel infoPlusApprovedTermination = controller.decisionInfoPlus(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
            .terminationDate(TERMINATION_DATE)
            .build(), terminationWorkflow.getId());
    assertThat(infoPlusApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);

    TerminationStopPointWorkflowModel novaDisapprovedTermination = controller.decisionNova(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.NO)
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
            .terminationDate(TERMINATION_DATE)
            .build(), terminationWorkflow.getId());
    assertThat(novaDisapprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED);
    verify(sePoDiAdminClient).changeToTariffStop(any());

    TerminationStopPointWorkflowModel novaApprovedTermination = controller.decisionNova(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
            .terminationDate(TERMINATION_DATE)
            .build(), terminationWorkflow.getId());
    assertThat(novaApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
  }
}