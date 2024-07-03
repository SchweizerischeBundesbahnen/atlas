package ch.sbb.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.StopPointWorkflowTestData;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Otp;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.helper.OtpHelper;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.OtpRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.service.sepodi.SePoDiClientService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class StopPointWorkflowControllerTest extends BaseControllerApiTest {

  static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowController controller;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private OtpRepository otpRepository;

  @MockBean
  private SePoDiClientService sePoDiClientService;

  @MockBean
  private StopPointWorkflowNotificationService notificationService;

  @AfterEach
  void tearDown() {
    otpRepository.deleteAll();
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @Test
  void shouldGetWorkflows() throws Exception {

    StopPointAddWorkflowModel workflowModel = StopPointWorkflowTestData.getAddStopPointWorkflow1();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel.getSloid(), workflowModel.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel);

    mvc.perform(get("/v1/stop-point/workflows"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)))
        .andExpect(jsonPath("$.objects[0].examinants", hasSize(3)));
  }

  @Test
  void shouldFindWorkflowsByFilterSloid() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?sloids=ch:1:sloid:1234"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldFindWorkflowsByFilterLocalityName() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?localityName=Bern"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldFindWorkflowsByFilterDesignation() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?designationOfficial=Aargau Strasse"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldFindWorkflowsByFilterVersionValidFrom() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?versionValidFrom=2010-12-11"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldFindWorkflowsByFilterStatus() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?status=ADDED"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(2)));
  }

  @Test
  void shouldFindWorkflowsByFilterSboid() throws Exception {
    StopPointAddWorkflowModel workflowModel1 = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    StopPointAddWorkflowModel workflowModel2 = StopPointWorkflowTestData.getAddStopPointWorkflow2();

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel1.getSloid(), workflowModel1.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel2.getSloid(), workflowModel2.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel2(Status.IN_REVIEW));

    controller.addStopPointWorkflow(workflowModel1);
    controller.addStopPointWorkflow(workflowModel2);

    mvc.perform(get("/v1/stop-point/workflows"
            + "?sboid=ch:1:sboid:100900"
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(2)));
  }

  @Test
  void shouldGetWorkflowById() throws Exception {
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    StopPointWorkflow workflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(person))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.ADDED)
        .build();

    StopPointWorkflow entity = workflowRepository.save(workflow);

    mvc.perform(get("/v1/stop-point/workflows/" + entity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sloid", is("ch:1:sloid:1234")));
  }

  @Test
  void shouldNotCreateWorkflowWhenWorkflowPersonNameHasWrongEncoding() throws Exception {
    //when
    StopPointClientPersonModel person = StopPointClientPersonModel.builder()
        .firstName("\uD83D\uDE00\uD83D\uDE01\uD83D")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .organisation("BAV")
        .mail(MAIL_ADDRESS).build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .applicantMail("a@b.ch")
        .examinants(List.of(person))
        .versionId(123456L)
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value \uD83D\uDE00\uD83D\uDE01? rejected due to must match "
            + "\"[\\u0000-\\u00ff]*\"")))
        .andExpect(jsonPath("$.details[0].field", is("examinants[0].firstName")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("\uD83D\uDE00\uD83D\uDE01?")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must match \"[\\u0000-\\u00ff]*\"")));
  }

  @Test
  void shouldNotCreateWorkflowWhenWorkflowPersonMandatoryDataIsEmpty() throws Exception {
    //when
    StopPointClientPersonModel person = StopPointClientPersonModel.builder().build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(List.of(person))
        .applicantMail("a@s.ch")
        .versionId(123456L)
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value null rejected due to must not be blank")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("null")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must not be blank")))
        .andExpect(jsonPath("$.details[1].message", is("Value null rejected due to must not be blank")))
        .andExpect(jsonPath("$.details[1].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[1].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[1].displayInfo.parameters[0].value", is("null")))
        .andExpect(jsonPath("$.details[1].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[1].displayInfo.parameters[1].value", is("must not be blank")));
  }

  @Test
  void shouldAddWorkflow() throws Exception {
    //when
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
    when(sePoDiClientService.updateStopPointStatusToInReview(sloid, versionId))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    //given
    mvc.perform(post("/v1/stop-point/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated());
  }

  @Test
  void shouldNotAddWorkflowWhenStopPointVersionIdNotFound() throws Exception {
    //when
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
    when(sePoDiClientService.updateStopPointStatusToInReview(sloid, versionId))
        .thenThrow(new IdNotFoundException(versionId));

    //given
    mvc.perform(post("/v1/stop-point/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with id " + versionId + " not found")))
        .andExpect(jsonPath("$.details[0].field", is("id")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("id")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is(String.valueOf(versionId))));
  }

  @Test
  void shouldNotAddWhenWorkflowAlreadyInStatusAdded() throws Exception {
    //when
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();

    Long versionId = 123456L;
    String sloid = "ch:1:sloid:1234";
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid(sloid)
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

    StopPointClientPersonModel personModel = StopPointClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .organisation("BAV")
        .mail(MAIL_ADDRESS).build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid(sloid)
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(List.of(personModel))
        .applicantMail("a@b.ch")
        .versionId(versionId)
        .build();
    when(sePoDiClientService.updateStopPointStatusToInReview(sloid, versionId))
        .thenReturn(getUpdateServicePointVersionModel(Status.IN_REVIEW));

    //given
    mvc.perform(post("/v1/stop-point/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isPreconditionRequired())
        .andExpect(jsonPath("$.status", is(428)))
        .andExpect(jsonPath("$.message", is("Workflow already in status ADDED")))
        .andExpect(jsonPath("$.error", is("StopPoint Workflow error")))
        .andExpect(jsonPath("$.details[0].message", is("Wrong status")))
        .andExpect(jsonPath("$.details[0].field", is("status")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("WORKFLOW.ERROR.WRONG_CHANGING_STATUS")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("status")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("ADDED")));
  }

  @Test
  void shouldNotCreateWorkflowWhenWorkflowWorkflowDescriptionHasWrongEncoding() throws Exception {
    //when
    StopPointClientPersonModel person = StopPointClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .organisation("BAV")
        .mail(MAIL_ADDRESS).build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("\uD83D\uDE00\uD83D\uDE01\uD83D")
        .examinants(List.of(person))
        .applicantMail("a@b.ch")
        .versionId(123456L)
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value \uD83D\uDE00\uD83D\uDE01? rejected due to must match "
            + "\"[\\u0000-\\u00ff]*\"")))
        .andExpect(jsonPath("$.details[0].field", is("workflowComment")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("\uD83D\uDE00\uD83D\uDE01?")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must match \"[\\u0000-\\u00ff]*\"")));
  }

  @Test
  void shouldStartWorkflow() throws Exception {
    //when
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

    //given
    mvc.perform(post("/v1/stop-point/workflows/start/" + stopPointWorkflow.getId())
            .contentType(contentType))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("HEARING")));
    verify(notificationService).sendStartStopPointWorkflowMail(any(StopPointWorkflow.class));
  }

  @Test
  void shouldEditWorkflow() throws Exception {
    //when
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
    EditStopPointWorkflowModel editStopPointWorkflowModel = EditStopPointWorkflowModel.builder()
        .workflowComment("New Comment")
        .designationOfficial("Bern")
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/edit/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(editStopPointWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ADDED")));
    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.ADDED);
    assertThat(workflows.get(0).getWorkflowComment()).isEqualTo(editStopPointWorkflowModel.getWorkflowComment());
  }

  @Test
  void shouldRejectWorkflow() throws Exception {
    //when
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

    //given
    mvc.perform(post("/v1/stop-point/workflows/reject/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointRejectWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("REJECTED")));

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.REJECTED);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNotNull();
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
    assertThat(decisionResult.getMotivation()).isEqualTo(stopPointRejectWorkflowModel.getMotivationComment());
    assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.REJECTED);

    verify(notificationService).sendRejectStopPointWorkflowMail(any(StopPointWorkflow.class), anyString());
  }

  @Test
  void shouldCancelWorkflow() throws Exception {
    //when
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

    //given
    mvc.perform(post("/v1/stop-point/workflows/cancel/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCancelWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("CANCELED")));

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.CANCELED);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNotNull();
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
    assertThat(decisionResult.getMotivation()).isEqualTo(stopPointCancelWorkflowModel.getMotivationComment());
    assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.CANCELED);
  }

  @Test
  void shouldAddExaminantToWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.save(workflow);

    StopPointClientPersonModel examinant = StopPointClientPersonModel.builder()
        .firstName("Luca")
        .lastName("Fix")
        .organisation("Org")
        .personFunction("YB-Fun")
        .mail(MAIL_ADDRESS).build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/add-examinant/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(examinant)))
        .andExpect(status().isOk());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getExaminants()).hasSize(2);
  }

  @Test
  void shouldGetOtpWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.save(workflow);

    OtpRequestModel otpRequest = OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/obtain-otp/" + stopPointWorkflow.getId())
            .contentType(contentType)
        .content(mapper.writeValueAsString(otpRequest)))
        .andExpect(status().isAccepted());

    Otp otpResult = otpRepository.findAll().stream().filter(otp -> otp.getPerson().getId().equals(person.getId())).findFirst()
        .orElse(null);

    assertThat(otpResult).isNotNull();
    assertThat(otpResult.getPerson().getId()).isEqualTo(person.getId());
  }

  @Test
  void shouldRemoveExaminantToWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.save(workflow);

    //given
    mvc.perform(post("/v1/stop-point/workflows/remove-examinant/" + stopPointWorkflow.getId() + "/" + person.getId())
            .contentType(contentType))
        .andExpect(status().isOk());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getExaminants()).isEmpty();
  }

  @Test
  void shouldVoteToWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.saveAndFlush(workflow);

    Otp otp = Otp.builder().code(OtpHelper.hashPinCode("12345")).person(person).build();
    otpRepository.saveAndFlush(otp);
    DecisionModel decisionModel = DecisionModel.builder()
        .judgement(JudgementType.NO)
        .motivation("Perfetto")
        .pinCode("12345")
        .examinantMail(MAIL_ADDRESS)
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .organisation("Napoli")
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/vote/" + stopPointWorkflow.getId() + "/" + person.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(decisionModel)))
        .andExpect(status().isOk());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getExaminants()).hasSize(1);
    Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
    assertThat(decisionByExaminantId).isNotNull();
    assertThat(decisionByExaminantId.getMotivation()).isEqualTo(decisionModel.getMotivation());
    assertThat(decisionByExaminantId.getJudgement()).isEqualTo(decisionModel.getJudgement());
    assertThat(decisionByExaminantId.getDecisionType()).isEqualTo(DecisionType.VOTED);
  }

  @Test
  void shouldRestartWorkflow() throws Exception {
    //when
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

    ClientPersonModel examinantBAV = ClientPersonModel.builder()
        .firstName("Luca")
        .lastName("Fix")
        .personFunction("YB-Fun")
        .mail(MAIL_ADDRESS).build();
    StopPointRestartWorkflowModel restartWorkflowModel = StopPointRestartWorkflowModel.builder()
        .examinantBAVClient(examinantBAV)
        .newDesignationOfficial("Bern")
        .motivationComment("Bern is better")
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/restart/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(restartWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ADDED")));
    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(2);
    assertThat(workflows.get(0).getStatus()).isEqualTo(WorkflowStatus.REJECTED);
    assertThat(workflows.get(0).getFollowUpWorkflow()).isNotNull();
    assertThat(workflows.get(1).getStatus()).isEqualTo(WorkflowStatus.ADDED);
  }

  @Test
  void shouldOverrideVoteWihtoutDecisionToWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.saveAndFlush(workflow);

    OverrideDecisionModel overrideDecisionModel = OverrideDecisionModel.builder()
        .firstName("Luca")
        .lastName("Fix")
        .fotJudgement(JudgementType.NO)
        .fotMotivation("Ja save")
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/override-vote/" + stopPointWorkflow.getId() + "/" + person.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(overrideDecisionModel)))
        .andExpect(status().isOk());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    Set<Person> examinants = workflows.get(0).getExaminants();
    assertThat(examinants).hasSize(1);
    Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
    assertThat(decisionByExaminantId).isNotNull();
    assertThat(decisionByExaminantId.getFotMotivation()).isEqualTo(overrideDecisionModel.getFotMotivation());
    assertThat(decisionByExaminantId.getFotJudgement()).isEqualTo(overrideDecisionModel.getFotJudgement());
  }

  @Test
  void shouldOverrideVoteWitDecisionToWorkflow() throws Exception {
    //when
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
    StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.save(workflow);

    Otp otp = Otp.builder().code("12345").person(person).build();
    otpRepository.save(otp);
    Decision decision = Decision.builder()
        .judgement(JudgementType.YES)
        .motivation("Perfetto")
        .motivationDate(LocalDateTime.now())
        .build();
    decisionRepository.save(decision);
    decision.setExaminant(person);
    decisionRepository.save(decision);
    OverrideDecisionModel overrideDecisionModel = OverrideDecisionModel.builder()
        .firstName("Luca")
        .lastName("Fix")
        .fotJudgement(JudgementType.NO)
        .fotMotivation("Ja save")
        .build();

    //given
    mvc.perform(post("/v1/stop-point/workflows/override-vote/" + stopPointWorkflow.getId() + "/" + person.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(overrideDecisionModel)))
        .andExpect(status().isOk());

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.get(0).getExaminants()).hasSize(1);
    Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
    assertThat(decisionByExaminantId).isNotNull();
    assertThat(decisionByExaminantId.getMotivation()).isEqualTo(decision.getMotivation());
    assertThat(decisionByExaminantId.getJudgement()).isEqualTo(decision.getJudgement());
    assertThat(decisionByExaminantId.getFotMotivation()).isEqualTo(overrideDecisionModel.getFotMotivation());
    assertThat(decisionByExaminantId.getFotJudgement()).isEqualTo(overrideDecisionModel.getFotJudgement());
  }

  private static ReadServicePointVersionModel getUpdateServicePointVersionModel(Status status) {
    long versionId = 123456L;
    String sloid = "ch:1:sloid:1234";
    ServicePointGeolocationReadModel geolocationReadModel = ServicePointGeolocationReadModel.builder()
        .swissLocation(SwissLocation.builder()
            .canton(SwissCanton.BERN)
            .localityMunicipality(LocalityMunicipalityModel.builder().localityName("Bern").build())
            .build())
        .build();
    return ReadServicePointVersionModel.builder()
        .designationLong("designation long 1")
        .designationOfficial("Aargau Strasse")
        .abbreviation("ABC")
        .id(versionId)
        .sloid(sloid)
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100871")
        .categories(List.of(Category.POINT_OF_SALE))
        .status(status)
        .servicePointGeolocation(geolocationReadModel)
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

  private static ReadServicePointVersionModel getUpdateServicePointVersionModel2(Status status) {
    long versionId = 654321L;
    String sloid = "ch:1:sloid:4321";
    ServicePointGeolocationReadModel geolocationReadModel = ServicePointGeolocationReadModel.builder()
        .swissLocation(SwissLocation.builder()
            .canton(SwissCanton.ZURICH)
            .localityMunicipality(LocalityMunicipalityModel.builder().localityName("Zürich").build())
            .build())
        .build();
    return ReadServicePointVersionModel.builder()
        .designationLong("Designer")
        .designationOfficial("Stroosse")
        .abbreviation("ABC")
        .id(versionId)
        .sloid(sloid)
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100900")
        .categories(List.of(Category.POINT_OF_SALE))
        .status(status)
        .servicePointGeolocation(geolocationReadModel)
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .validFrom(LocalDate.of(2008, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

}