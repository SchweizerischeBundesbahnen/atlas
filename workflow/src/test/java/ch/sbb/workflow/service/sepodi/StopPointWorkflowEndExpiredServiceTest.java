package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
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
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointWorkflowEndExpiredServiceTest {

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private StopPointWorkflowEndExpiredService stopPointWorkflowEndExpiredService;

  @MockBean
  private SePoDiClientService sePoDiClientService;

  @MockBean
  private StopPointWorkflowNotificationService notificationService;

  static final int WORKFLOW_EXPIRATION_IN_DAYS = StopPointWorkflowTransitionService.WORKFLOW_DURATION_IN_DAYS;

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
    ReadServicePointVersionModel readServicePointVersionModel = ReadServicePointVersionModel.builder().build();
    doReturn(readServicePointVersionModel).when(sePoDiClientService)
        .updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
  }

  @AfterEach
  void tearDown() {
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @Test
  void shouldEndWorkflowWithNoVotes() {
    //given
    workflowInHearing.setEndDate(LocalDate.now().minusDays(1));
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1));
    workflowRepository.save(workflowInHearing);
    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
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
  void shouldNotEndWorkflowWhenEndDateIsLessThan31Days() {
    //given
    workflowInHearing.setStartDate(LocalDate.now().minusDays(30));
    workflowInHearing.setEndDate(LocalDate.now());
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService, never()).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
    verify(notificationService, never()).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.HEARING);
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
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1));
    LocalDate endDate = LocalDate.now().minusDays(1);
    workflowInHearing.setEndDate(endDate);
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
    assertThat(workflowInHearing.getEndDate()).isEqualTo(endDate);
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
  void shouldEndExpiredWorkflowsAfterMoreThan31Days() {
    //given
    Decision mareksDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(marek)
        .build();
    decisionRepository.save(mareksDecision);
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 3));
    workflowInHearing.setEndDate(LocalDate.now().minusDays(2));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
    verify(notificationService).sendApprovedStopPointWorkflowMail(workflowInHearing);
    StopPointWorkflow result = workflowRepository.getReferenceById(workflowInHearing.getId());
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
    assertThat(workflowInHearing.getEndDate()).isEqualTo(LocalDate.now());
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

    workflowInHearing.setEndDate(LocalDate.now().minusDays(1));
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
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

    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1));
    workflowInHearing.setEndDate(LocalDate.now().minusDays(1));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
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
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1));
    workflowInHearing.setEndDate(LocalDate.now().minusDays(1));
    workflowRepository.save(workflowInHearing);

    //when
    stopPointWorkflowEndExpiredService.endExpiredWorkflows();

    //then
    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdminForJob(workflowInHearing);
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

  @Test
  void shouldGetExpiredWorkflowsAfterMoreThane31Days() {
    //given
    workflowInHearing.setStartDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS));
    workflowInHearing.setEndDate(LocalDate.now());
    workflowRepository.save(workflowInHearing);
    StopPointWorkflow stopPointWorkflow32Days = StopPointWorkflow.builder().designationOfficial("De1")
        .sloid("ch:1:sloid:1235")
        .sboid("ch:1:sboid:665")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(new HashSet<>(Set.of(marek, judith)))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .startDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 1))
        .endDate(LocalDate.now().minusDays(1))
        .build();
    workflowRepository.save(stopPointWorkflow32Days);
    StopPointWorkflow stopPointWorkflow42Days = StopPointWorkflow.builder().designationOfficial("De2").sloid("ch:1:sloid:1236")
        .sboid("ch:1:sboid:667")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(new HashSet<>(Set.of(marek, judith)))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .startDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS + 10))
        .endDate(LocalDate.now().minusDays(2))
        .build();
    workflowRepository.save(stopPointWorkflow42Days);
    StopPointWorkflow stopPointWorkflow30Days = StopPointWorkflow.builder().designationOfficial("De3").sloid("ch:1:sloid:1238")
        .sboid("ch:1:sboid:668")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(new HashSet<>(Set.of(marek, judith)))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .startDate(LocalDate.now().minusDays(WORKFLOW_EXPIRATION_IN_DAYS - 1))
        .endDate(LocalDate.now().plusDays(1))
        .build();
    workflowRepository.save(stopPointWorkflow30Days);
    //when
    List<StopPointWorkflow> results = stopPointWorkflowEndExpiredService.getExpiredWorkflows();

    //then
    assertThat(results)
        .hasSize(2)
        .containsExactlyInAnyOrder(stopPointWorkflow42Days, stopPointWorkflow32Days);
  }

}