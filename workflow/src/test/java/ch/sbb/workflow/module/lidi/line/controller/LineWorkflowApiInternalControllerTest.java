package ch.sbb.workflow.module.lidi.line.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.line.workflow.LineWorkflowClient;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.PersonModel;
import ch.sbb.atlas.api.workflow.WorkflowModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.lidi.line.api.LineWorkflowApiInternal;
import ch.sbb.workflow.module.lidi.line.entity.LineWorkflow;
import ch.sbb.workflow.module.lidi.line.repository.WorkflowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class LineWorkflowApiInternalControllerTest extends BaseControllerApiTest {

  private static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private LineWorkflowApiInternalController controller;

  @Autowired
  private WorkflowRepository workflowRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @MockitoBean
  private LineWorkflowClient lineWorkflowClient;

  @BeforeEach
  void setUp() {
    when(lineWorkflowClient.processWorkflow(any())).thenReturn(WorkflowStatus.STARTED);
  }

  @AfterEach
  void tearDown() {
    workflowRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET internal/line/workflows/{id}")
  class GetWorkflow {

    @Test
    void shouldGetWorkflowById() throws Exception {
      getWorkflowById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.swissId", is("CH123456")))
          .andExpect(jsonPath("$.client.mail", is(MAIL_ADDRESS)));
    }

    private ResultActions getWorkflowById() throws Exception {
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      LineWorkflow lineWorkflow = LineWorkflow.builder()
          .client(person)
          .examinant(person)
          .swissId("CH123456")
          .status(WorkflowStatus.ADDED)
          .examinant(person)
          .workflowType(WorkflowType.LINE)
          .description("ch:123:431")
          .workflowComment("comment")
          .checkComment("comment")
          .businessObjectId(123456L)
          .number("IC8")
          .build();

      LineWorkflow entity = workflowRepository.saveAndFlush(lineWorkflow);

      return mvc.perform(get(LineWorkflowApiInternal.BASE_PATH + "/" + entity.getId()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetWorkflowByIdAsStandardUser() throws Exception {
      getWorkflowById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.swissId", is("CH123456")))
          .andExpect(jsonPath("$.client.mail", is(MAIL_ADDRESS)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetWorkflowByIdAsUnauthorized() throws Exception {
      getWorkflowById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.swissId", is("CH123456")))
          .andExpect(jsonPath("$.client.mail", is("m*****")));
    }
  }

  @Nested
  @DisplayName("GET internal/line/workflows")
  class GetWorkflows {

    @Test
    void shouldGetWorkflows() throws Exception {
      getWorkflows()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }

    private ResultActions getWorkflows() throws Exception {
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      LineWorkflow lineWorkflow = LineWorkflow.builder()
          .client(person)
          .examinant(person)
          .swissId("CH123456")
          .status(WorkflowStatus.ADDED)
          .examinant(person)
          .workflowType(WorkflowType.LINE)
          .description("ch:123:431")
          .workflowComment("comment")
          .checkComment("comment")
          .businessObjectId(123456L)
          .number("IC8")
          .build();

      workflowRepository.saveAndFlush(lineWorkflow);

      return mvc.perform(get(LineWorkflowApiInternal.BASE_PATH));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetWorkflowsWithoutSensitiveInformationForUnauthorized() throws Exception {
      getWorkflows()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].client.mail", is("m*****")))
          .andExpect(jsonPath("$[0].client.firstName", is("M*****")))
          .andExpect(jsonPath("$[0].client.lastName", is("H*****")));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetWorkflowsAsStandardUser() throws Exception {
      getWorkflows()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].client.mail", is(MAIL_ADDRESS)));
    }
  }

  @Nested
  @DisplayName("POST internal/line/workflows")
  class StartWorkflow {

    @Test
    void shouldCreateWorkflow() throws Exception {
      createWorkflow().andExpect(status().isCreated());
    }

    private ResultActions createWorkflow() throws Exception {
      //when
      ClientPersonModel person = ClientPersonModel.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .personFunction("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      WorkflowModel workflowModel = WorkflowModel.builder()
          .client(person)
          .examinant(person)
          .swissId("CH123456")
          .examinant(person)
          .description("ch:123:431")
          .workflowComment("comment")
          .checkComment("comment")
          .workflowType(WorkflowType.LINE)
          .businessObjectId(123456L)
          .number("IC8")
          .build();

      //given
      return mvc.perform(post(LineWorkflowApiInternal.BASE_PATH)
          .contentType(contentType)
          .content(mapper.writeValueAsString(workflowModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldCreateWorkflowAsStandardUser() throws Exception {
      createWorkflow().andExpect(status().isCreated());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCreateWorkflowAsUnauthorized() throws Exception {
      createWorkflow().andExpect(status().isForbidden());
    }

    @Test
    void shouldNotCreateWorkflowWhenWorkflowTypeIsNull() throws Exception {
      //when
      ClientPersonModel person = ClientPersonModel.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .personFunction("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      WorkflowModel workflowModel = WorkflowModel.builder()
          .client(person)
          .examinant(person)
          .description("desc")
          .swissId("CH123456")
          .examinant(person)
          .businessObjectId(123456L)
          .build();

      //given
      mvc.perform(post(LineWorkflowApiInternal.BASE_PATH)
              .contentType(contentType)
              .content(mapper.writeValueAsString(workflowModel))
          ).andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(
              jsonPath("$.message",
                  is("Following constraints were violated: [Property 'workflowType' has invalid value: 'null']")))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.details[0].message", is("must not be null")))
          .andExpect(jsonPath("$.details[0].field", is("workflowType")))
          .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.NOT_NULL")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("propertyPath")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("workflowType")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("null")));
    }

    @Test
    void shouldNotCreateWorkflowWhenWorkflowPersonNameHasWrongEncoding() throws Exception {
      //when
      ClientPersonModel person = ClientPersonModel.builder()
          .firstName("\uD83D\uDE00\uD83D\uDE01\uD83D")
          .lastName("Hamsik")
          .personFunction("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      WorkflowModel workflowModel = WorkflowModel.builder()
          .client(person)
          .workflowType(WorkflowType.LINE)
          .examinant(person)
          .description("desc")
          .swissId("CH123456")
          .examinant(person)
          .businessObjectId(123456L)
          .build();

      //given
      mvc.perform(post(LineWorkflowApiInternal.BASE_PATH)
              .contentType(contentType)
              .content(mapper.writeValueAsString(workflowModel))
          ).andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'client.firstName' has invalid value: "
                  + "'\uD83D\uDE00\uD83D\uDE01?']")))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.details[0].message", is("must match \"[\\u0000-\\u00ff]*\"")))
          .andExpect(jsonPath("$.details[0].field", is("client.firstName")))
          .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.PATTERN")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("propertyPath")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("client.firstName")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("\uD83D\uDE00\uD83D\uDE01?")));
    }

    @Test
    void shouldNotCreateWorkflowWhenWorkflowWorkflowDescriptionHasWrongEncoding() throws Exception {
      //when
      ClientPersonModel person = ClientPersonModel.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .personFunction("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      WorkflowModel workflowModel = WorkflowModel.builder()
          .client(person)
          .workflowType(WorkflowType.LINE)
          .examinant(person)
          .description("\uD83D\uDE00\uD83D\uDE01\uD83D")
          .swissId("CH123456")
          .examinant(person)
          .businessObjectId(123456L)
          .build();

      //given
      mvc.perform(post(LineWorkflowApiInternal.BASE_PATH)
              .contentType(contentType)
              .content(mapper.writeValueAsString(workflowModel))
          ).andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'description' has invalid value: '\uD83D\uDE00\uD83D\uDE01?']")))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.details[0].message", is("must match \"[\\u0000-\\u00ff]*\"")))
          .andExpect(jsonPath("$.details[0].field", is("description")))
          .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.PATTERN")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("propertyPath")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("description")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
          .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("\uD83D\uDE00\uD83D\uDE01?")));
    }
  }

  @Nested
  @DisplayName("POST internal/line/workflows/{id}/examinant-check")
  class ExaminantCheck {

    @Test
    void shouldAcceptWorkflow() throws Exception {
      acceptWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.checkComment", is("ok")));
    }

    private ResultActions acceptWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();
      LineWorkflow lineWorkflow = LineWorkflow.builder()
          .client(person)
          .examinant(person)
          .swissId("CH123456")
          .status(WorkflowStatus.STARTED)
          .examinant(person)
          .workflowType(WorkflowType.LINE)
          .description("ch:123:431")
          .workflowComment("comment")
          .checkComment("comment")
          .businessObjectId(123456L)
          .number("IC8")
          .build();

      LineWorkflow startedWorkflow = workflowRepository.saveAndFlush(lineWorkflow);

      ExaminantWorkflowCheckModel workflowCheck = ExaminantWorkflowCheckModel.builder()
          .accepted(true).checkComment("ok").examinant(PersonModel.builder()
              .firstName("Marek")
              .lastName("Hamsik")
              .personFunction("Centrocampista")
              .build())
          .build();

      //given
      return mvc.perform(post(LineWorkflowApiInternal.BASE_PATH + "/" + startedWorkflow.getId() + "/examinant-check")
          .contentType(contentType)
          .content(mapper.writeValueAsString(workflowCheck)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAcceptWorkflowAsSupervisor() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.LIDI)
          .role(ApplicationRole.SUPERVISOR)
          .build();
      permissionRepository.saveAndFlush(permission);

      acceptWorkflow().andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotAcceptWorkflowAsStandardUser() throws Exception {
      acceptWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAcceptWorkflowAsUnauthorized() throws Exception {
      acceptWorkflow().andExpect(status().isForbidden());
    }
  }

}