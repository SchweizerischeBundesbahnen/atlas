package ch.sbb.workflow.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.security.service.ServicePointTerminationBasedUserAdministrationService;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
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

class TerminationStopPointWorkflowInternalControllerVotingTest extends BaseControllerApiTest {

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
        .build();

    when(sePoDiAdminClient.postStartServicePointTermination(any(), any(), any(
        UpdateTerminationServicePointModel.class))).thenReturn(servicePointVersionModel);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void shouldFlowThroughApprovedStatuses() {
    TerminationStopPointWorkflowModel terminationWorkflow = controller.startTerminationStopPointWorkflow(
        StartTerminationStopPointWorkflowModel.builder()
            .workflowComment("Please terminate this stop point")
            .applicantMail("user@tu.ch")
            .boTerminationDate(LocalDate.of(2020, 1, 1))
            .sloid("ch:1:sboid:132")
            .versionId(123L)
            .build());
    assertThat(terminationWorkflow.getStatus()).isEqualTo(TerminationWorkflowStatus.STARTED);

    TerminationStopPointWorkflowModel infoPlusApprovedTermination = controller.decisionInfoPlus(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
            .terminationDate(LocalDate.of(2020, 1, 2))
            .build(), terminationWorkflow.getId());
    assertThat(infoPlusApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TARIFF_STOP_APPROVED);

    TerminationStopPointWorkflowModel novaApprovedTermination = controller.decisionNova(
        TerminationDecisionModel.builder()
            .judgement(JudgementType.YES)
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
            .terminationDate(LocalDate.of(2020, 1, 2))
            .build(), terminationWorkflow.getId());
    assertThat(novaApprovedTermination.getStatus()).isEqualTo(TerminationWorkflowStatus.TERMINATION_APPROVED);
  }

}