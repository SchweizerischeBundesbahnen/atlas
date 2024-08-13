package ch.sbb.workflow.service.sepodi;

import static ch.sbb.workflow.service.sepodi.StopPointWorkflowService.WORKFLOW_DURATION_IN_DAYS;
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
import java.util.HashSet;
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
        .examinants(new HashSet<>(Set.of(marek, judith)))
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

    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(ArgumentMatchers.any());
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

  @Test
  void shouldEndWorkflowWithNoVotes() {
    //given
    workflowInHearing.setEndDate(LocalDate.now().minusDays(WORKFLOW_DURATION_IN_DAYS));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowTransitionService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
    workflowInHearing.getExaminants().forEach(person -> {
      Decision decision = decisionRepository.findDecisionByExaminantId(person.getId());
      assertThat(decision.getDecisionType()).isEqualTo(DecisionType.VOTED_EXPIRATION);
      assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);
    });
  }

  @Test
  void shouldEndWorkflowWithOneExaminantVotedYes() {
    //given
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(marek)
        .build();
    decisionRepository.save(mareksDecision);

    workflowInHearing.setEndDate(LocalDate.now().minusDays(WORKFLOW_DURATION_IN_DAYS));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowTransitionService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
    workflowInHearing.getExaminants().forEach(person -> {
      Decision decision = decisionRepository.findDecisionByExaminantId(person.getId());
      if (person.getId().equals(marek.getId())) {
        assertThat(decision.getDecisionType()).isEqualTo(DecisionType.VOTED);
      } else {
        assertThat(decision.getDecisionType()).isEqualTo(DecisionType.VOTED_EXPIRATION);
      }
      assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);
    });
  }

  @Test
  void shouldEndWorkflowWithOneBavExaminantVotedYes() {
    //given
    Decision mareksDecision = Decision.builder()
        .decisionType(DecisionType.VOTED)
        .fotJudgement(JudgementType.YES)
        .fotOverrider(marek)
        .build();
    decisionRepository.save(mareksDecision);

    workflowInHearing.setEndDate(LocalDate.now().minusDays(WORKFLOW_DURATION_IN_DAYS));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowTransitionService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
    Set<Decision> decisionResults = decisionRepository.findDecisionByWorkflowId(workflowInHearing.getId());
    assertThat(decisionResults).hasSize(2);

    Decision judithDecision =
        decisionResults.stream().filter(decision -> judith.equals(decision.getExaminant())).toList()
            .getFirst();
    assertThat(judithDecision).isNotNull();
    assertThat(judithDecision.getDecisionType()).isEqualTo(DecisionType.VOTED_EXPIRATION);
    assertThat(judithDecision.getJudgement()).isEqualTo(JudgementType.YES);
    Decision marekDecision =
        decisionResults.stream().filter(decision -> marek.equals(decision.getFotOverrider())).toList().getFirst();
    assertThat(marekDecision).isNotNull();
    assertThat(marekDecision.getDecisionType()).isEqualTo(DecisionType.VOTED);
    assertThat(marekDecision.getFotJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldEndWorkflowWithExaminantAndBavVotedYes() {
    //given
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .fotJudgement(JudgementType.YES)
        .examinant(marek)
        .fotOverrider(judith)
        .build();
    decisionRepository.save(mareksDecision);

    workflowInHearing.setEndDate(LocalDate.now().minusDays(WORKFLOW_DURATION_IN_DAYS));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowTransitionService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);

    Set<Decision> decisionResults = new HashSet<>(decisionRepository.findDecisionByWorkflowId(workflowInHearing.getId()));
    assertThat(decisionResults).hasSize(1);
    Decision firstDecision = decisionResults.stream().toList().getFirst();
    assertThat(firstDecision.getDecisionType()).isEqualTo(DecisionType.VOTED);
    assertThat(firstDecision.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(firstDecision.getFotJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldEndWorkflowWithTwoExaminantsAndBavVotedYes() {
    //given
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .fotJudgement(JudgementType.YES)
        .examinant(marek)
        .fotOverrider(judith)
        .build();
    decisionRepository.save(mareksDecision);

    Person cianni = Person.builder()
        .firstName("Cianni")
        .lastName("Quattro Staccioni")
        .function("Pizza")
        .mail("cianni@staccioni.com").build();
    cianni.setStopPointWorkflow(workflowInHearing);
    workflowInHearing.getExaminants().add(cianni);
    workflowInHearing.setEndDate(LocalDate.now().minusDays(WORKFLOW_DURATION_IN_DAYS));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowTransitionService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);

    Set<Decision> decisionResults = new HashSet<>(decisionRepository.findDecisionByWorkflowId(workflowInHearing.getId()));
    assertThat(decisionResults).hasSize(2);
    Decision marekDecision = decisionResults.stream().filter(decision -> decision.getExaminant().equals(marek)).findFirst()
        .orElseThrow();
    assertThat(marekDecision.getDecisionType()).isEqualTo(DecisionType.VOTED);
    assertThat(marekDecision.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(marekDecision.getFotJudgement()).isEqualTo(JudgementType.YES);

    Decision cianniDecision =
        decisionResults.stream().filter(decision -> decision.getExaminant().getFirstName().equals(cianni.getFirstName()))
            .findFirst().orElseThrow();
    assertThat(cianniDecision.getDecisionType()).isEqualTo(DecisionType.VOTED_EXPIRATION);
    assertThat(cianniDecision.getJudgement()).isEqualTo(JudgementType.YES);
    assertThat(cianniDecision.getFotJudgement()).isNull();
  }

}