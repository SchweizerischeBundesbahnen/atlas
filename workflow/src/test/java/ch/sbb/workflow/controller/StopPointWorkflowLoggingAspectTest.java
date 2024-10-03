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
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.service.sepodi.SePoDiClientService;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowTransitionService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class StopPointWorkflowLoggingAspectTest extends BaseControllerApiTest {

  static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private StopPointWorkflowTransitionService stopPointWorkflowTransitionService;

  @MockBean
  private SePoDiClientService sePoDiClientService;

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
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
    listAppender.stop();
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

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel.getSloid(), workflowModel.getVersionId()))
        .thenThrow(new IllegalStateException());

    // when & then
    mvc.perform(post("/v1/stop-point/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().is5xxServerError());

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.ADD_WORKFLOW +
                "\""));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldRejectWorkflowLoggingAspect() throws Exception {
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
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.REJECT_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldCancelWorkflowWithLoggingAspect() throws Exception {
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
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.CANCEL_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldOverridePendingVoteCorrectlyLoggingAspect() {
    assertThrows(IdNotFoundException.class,
        () -> stopPointWorkflowTransitionService.progressWorkflowWithNewDecision(100L));

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.WORKFLOW_TYPE_VOTE_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
  }

  @Test
  void shouldRestartWorkflowLoggingAspect() throws Exception {
    // given
    Person person = Person.builder()
        .firstName("Marek1")
        .lastName("Hamsik1")
        .function("Centrocampista1")
        .mail(MAIL_ADDRESS).build();

    Long versionId = 123456L;
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne unter 30")
        .localityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("Yet another WF comment")
        .status(WorkflowStatus.ADDED)
        .examinants(Set.of(person))
        .startDate(LocalDate.of(2001, 1, 1))
        .endDate(LocalDate.of(2001, 12, 31))
        .versionId(versionId)
        .build();
    workflowRepository.save(stopPointWorkflow);

    StopPointRestartWorkflowModel stopPointRestartWorkflowModel = StopPointRestartWorkflowModel.builder()
        .motivationComment("No Comment1")
        .firstName("Marek1")
        .lastName("Hamsik1")
        .organisation("YB1")
        .mail(MAIL_ADDRESS)
        .designationOfficial("NEWDESIGNATION")
        .build();

    // when & then
    mvc.perform(post("/v1/stop-point/workflows/restart/" + stopPointWorkflow.getId() + 5)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointRestartWorkflowModel)))
        .andExpect(status().isNotFound());

    boolean logFound = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains(LoggingAspect.ERROR_MARKER) &&
            event.getFormattedMessage().contains("\"workflowType\":" + "\"" + LoggingAspect.RESTART_WORKFLOW + "\""));
    assertThat(logFound).isTrue();
  }
}
