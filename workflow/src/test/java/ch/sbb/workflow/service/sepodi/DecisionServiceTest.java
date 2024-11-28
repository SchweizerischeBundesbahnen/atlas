package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class DecisionServiceTest {

  private static final String DANIEL = "Daniel";
  private static final String SBOID = "ch:1:sboid:666";

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private DecisionService decisionService;

  private StopPointWorkflow workflowInHearing;

  @AfterEach
  void tearDown() {
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    Person marek = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("mail@hamsik.cc").build();
    Person daniel = Person.builder()
        .firstName(DANIEL)
        .lastName("Djukic")
        .function("Gymbro")
        .mail("bro@gym.cc").build();
    StopPointWorkflow workflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid(SBOID)
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(marek, daniel))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .build();
    marek.setStopPointWorkflow(workflow);
    daniel.setStopPointWorkflow(workflow);

    workflowInHearing = workflowRepository.saveAndFlush(workflow);
  }

  @Test
  void shouldGetDecision() {
    Person daniel = workflowInHearing.getExaminants().stream().filter(i -> i.getFirstName().equals(DANIEL)).findFirst()
        .orElseThrow();
    Decision decision = Decision.builder()
        .judgement(JudgementType.YES)
        .motivation("Good Name!")
        .motivationDate(LocalDateTime.now())
        .examinant(daniel)
        .build();
    decisionRepository.saveAndFlush(decision);

    decision = decisionService.getDecisionByExaminantId(daniel.getId(), SBOID);
    assertThat(decision).isNotNull();
  }

  @Test
  void shouldThrowNotFoundException() {
    assertThatExceptionOfType(IdNotFoundException.class).isThrownBy(() -> decisionService.getDecisionByExaminantId(1L, SBOID));
  }

  @Test
  void shouldAddCalculatedJudgementWithFotPriorityToExaminantsWithNoDecision() {
    List<StopPointClientPersonModel> examinants = workflowInHearing.getExaminants().stream()
        .map(StopPointClientPersonMapper::toModel)
        .sorted(Comparator.comparing(StopPointClientPersonModel::getFirstName))
        .toList();

    decisionService.addJudgementsToExaminants(examinants);

    assertThat(examinants.getFirst().getJudgement()).isNull();
  }

  @Test
  void shouldAddCalculatedJudgementWithFotPriorityToExaminantsWithDecisionsByExaminantDaniel() {
    Person daniel = workflowInHearing.getExaminants().stream().filter(i -> i.getFirstName().equals(DANIEL)).findFirst()
        .orElseThrow();
    Decision decision = Decision.builder()
        .judgement(JudgementType.YES)
        .motivation("Good Name!")
        .motivationDate(LocalDateTime.now())
        .examinant(daniel)
        .build();
    decisionRepository.save(decision);

    List<StopPointClientPersonModel> examinants = workflowInHearing.getExaminants().stream()
        .map(StopPointClientPersonMapper::toModel)
        .sorted(Comparator.comparing(StopPointClientPersonModel::getFirstName))
        .toList();

    decisionService.addJudgementsToExaminants(examinants);

    assertThat(examinants.getFirst().getJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldAddCalculatedJudgementWithFotPriorityToExaminantsWithDecisionsByExaminantDanielAndOverride() {
    Person daniel = workflowInHearing.getExaminants().stream().filter(i -> i.getFirstName().equals(DANIEL)).findFirst()
        .orElseThrow();
    Decision decision = Decision.builder()
        .judgement(JudgementType.YES)
        .motivation("Good Name!")
        .motivationDate(LocalDateTime.now())
        .examinant(daniel)
        .fotJudgement(JudgementType.NO)
        .fotMotivation("No, is no!")
        .build();
    decisionRepository.save(decision);

    List<StopPointClientPersonModel> examinants = workflowInHearing.getExaminants().stream()
        .map(StopPointClientPersonMapper::toModel)
        .sorted(Comparator.comparing(StopPointClientPersonModel::getFirstName))
        .toList();

    decisionService.addJudgementsToExaminants(examinants);

    assertThat(examinants.getFirst().getJudgement()).isEqualTo(JudgementType.NO);
  }
}