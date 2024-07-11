package ch.sbb.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.aop.LoggingAspect;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.service.sepodi.SePoDiClientService;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowProgressDecider;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowTransitionService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class StopPointWorkflowControllerLoggingAspectTest extends BaseControllerApiTest {

  static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private StopPointWorkflowTransitionService stopPointWorkflowTransitionService;

  @Autowired
  private SePoDiClientService sePoDiClientService;

  @MockBean
  private StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider;

  private ListAppender<ILoggingEvent> listAppender;

  private StopPointWorkflow workflowInHearing;

  private Person judith;
  private Person marek;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    listAppender = new ListAppender<>();
    Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
    listAppender.start();
    logger.addAppender(listAppender);

    marek = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();
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
  }

  @AfterEach
  void tearDown() {
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @Test
  void shouldAddWorkflowLoggingAspect() throws Exception {
    // given
    StopPointClientPersonModel person = StopPointClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .organisation("BAV")
        .mail(MAIL_ADDRESS).build();
    long versionId = 123456L;
    String sloid = "ch:1:sloid:1234";
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid(sloid)
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(List.of(person))
        .applicantMail("a@b.ch")
        .versionId(versionId)
        .build();

    // when & then
    mvc.perform(post("/v1/stop-point/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().is5xxServerError());

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + StopPointWorkflowTransitionService.addWorkflow +
                "\"") &&
            event.getFormattedMessage().contains("\"isCritical\":true"));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldRejectWorkflowLoggingAspect() throws Exception {
    workflowRepository.deleteAll();
    // given
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();

    Long versionId = 123456L;
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .status(WorkflowStatus.ADDED)
        .examinants(Set.of(person))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(versionId)
        .build();
    workflowRepository.save(stopPointWorkflow);

    StopPointRejectWorkflowModel stopPointRejectWorkflowModel = StopPointRejectWorkflowModel.builder()
        .motivationComment("No Comment")
        .firstName("Marek")
        .lastName("Hamsik")
        .organisation("YB")
        .mail(MAIL_ADDRESS)
        .build();

    // when & then
    mvc.perform(post("/v1/stop-point/workflows/reject/" + stopPointWorkflow.getId() + 1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointRejectWorkflowModel)))
        .andExpect(status().isNotFound());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.ADDED);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNull();

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + StopPointWorkflowTransitionService.rejectWorkflow + "\"") &&
            event.getFormattedMessage().contains("\"isCritical\":true"));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldCancelWorkflowWithLoggingAspect() throws Exception {
    workflowRepository.deleteAll();
    // given
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();

    Long versionId = 123456L;
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .status(WorkflowStatus.HEARING)
        .examinants(Set.of(person))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(versionId)
        .build();
    workflowRepository.save(stopPointWorkflow);

    StopPointRejectWorkflowModel stopPointCancelWorkflowModel = StopPointRejectWorkflowModel.builder()
        .motivationComment("I don't like it!")
        .firstName("Marek")
        .lastName("Hamsik")
        .organisation("YB")
        .mail(MAIL_ADDRESS)
        .build();

    // when & then
    mvc.perform(post("/v1/stop-point/workflows/cancel/" + stopPointWorkflow.getId() + 1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCancelWorkflowModel)))
        .andExpect(status().isNotFound());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.HEARING);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNull();

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + StopPointWorkflowTransitionService.cancelWorkflow + "\"") &&
            event.getFormattedMessage().contains("\"isCritical\":true"));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldOverridePendingVoteCorrectlyLoggingAspect() throws Exception {
    // given
    Person examinantToOverride = workflowInHearing.getExaminants().stream().filter(i -> i.getMail().equals(MAIL_ADDRESS))
        .findFirst().orElseThrow();

    Decision examinantDecision = decisionRepository.findDecisionByExaminantId(examinantToOverride.getId());
    assertThat(examinantDecision).isNull();

    OverrideDecisionModel override = OverrideDecisionModel.builder()
        .firstName("Luca")
        .lastName("Ammann")
        .fotJudgement(JudgementType.YES)
        .fotMotivation("Nein, Müll")
        .build();

    when(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus())
        .thenReturn(Optional.of(WorkflowStatus.REJECTED));

    assertThrows(IdNotFoundException.class,
        () -> stopPointWorkflowTransitionService.progressWorkflowWithNewDecision(workflowInHearing.getId() + 1));

//    // when & then
//    mvc.perform(post("/v1/stop-point/workflows/override-vote/" + workflowInHearing.getId() + "/" + examinantToOverride.getId())
//            .contentType(contentType)
//            .content(mapper.writeValueAsString(override)))
//        .andExpect(status().isNotFound());



    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.workflowTypeVoteWorkflow + "\"") &&
            event.getFormattedMessage().contains("\"isCritical\":true"));
    assertThat(logFound).isTrue();
  }

}
