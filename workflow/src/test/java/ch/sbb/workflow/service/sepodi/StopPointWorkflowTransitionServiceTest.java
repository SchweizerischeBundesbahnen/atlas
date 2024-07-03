package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointWorkflowTransitionServiceTest {

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private StopPointWorkflowTransitionService stopPointWorkflowTransitionService;

  @MockBean
  private SePoDiClientService sePoDiClientService;

  @MockBean
  private StopPointWorkflowNotificationService notificationService;

  private StopPointWorkflow workflowInHearing;
  private Person marek;
  private Person judith;

  @BeforeEach
  void setUp() {
    marek = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("marek@hamsik.com").build();
    judith = Person.builder()
        .firstName("Judith")
        .lastName("Bollhalder")
        .function("Fachstelle")
        .mail("judith.bollhalder@sbb.ch").build();
    StopPointWorkflow workflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(marek, judith))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .build();
    marek.setStopPointWorkflow(workflow);
    judith.setStopPointWorkflow(workflow);

    workflowInHearing = workflowRepository.save(workflow);
    marek = workflowInHearing.getExaminants().stream().filter(i -> i.getFirstName().equals("Marek")).findFirst().orElseThrow();
    judith = workflowInHearing.getExaminants().stream().filter(i -> i.getFirstName().equals("Judith")).findFirst().orElseThrow();
  }

  @AfterEach
  void tearDown() {
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @Test
  void shouldDoNothingOnMissingVote() {
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(marek)
        .build();
    decisionRepository.save(mareksDecision);

    stopPointWorkflowTransitionService.progressWorkflowWithNewDecision(workflowInHearing.getId());

    workflowInHearing = workflowRepository.findById(workflowInHearing.getId()).orElseThrow();
    assertThat(workflowInHearing.getStatus()).isEqualTo(WorkflowStatus.HEARING);
  }

  @Test
  void shouldApproveOnAllDecisionsVotedAsYes() {
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(marek)
        .build();
    decisionRepository.save(mareksDecision);

    Decision judithsDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(judith)
        .build();
    decisionRepository.save(judithsDecision);

    stopPointWorkflowTransitionService.progressWorkflowWithNewDecision(workflowInHearing.getId());

    verify(sePoDiClientService).updateStoPointStatusToValidated(ArgumentMatchers.any());
    verify(notificationService).sendApprovedStopPointWorkflowMail(ArgumentMatchers.any());

    workflowInHearing = workflowRepository.findById(workflowInHearing.getId()).orElseThrow();
    assertThat(workflowInHearing.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
  }

  @Test
  void shouldRejectOnBavNo() {
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.NO)
        .decisionType(DecisionType.VOTED)
        .fotJudgement(JudgementType.NO)
        .fotMotivation("No is no!")
        .examinant(marek)
        .fotOverrider(judith)
        .build();
    decisionRepository.save(mareksDecision);

    stopPointWorkflowTransitionService.progressWorkflowWithNewDecision(workflowInHearing.getId());

    verify(sePoDiClientService).updateStopPointStatusToDraft(any());
    verify(notificationService).sendCanceledStopPointWorkflowMail(any(), any());

    workflowInHearing = workflowRepository.findById(workflowInHearing.getId()).orElseThrow();
    assertThat(workflowInHearing.getStatus()).isEqualTo(WorkflowStatus.REJECTED);
  }
}