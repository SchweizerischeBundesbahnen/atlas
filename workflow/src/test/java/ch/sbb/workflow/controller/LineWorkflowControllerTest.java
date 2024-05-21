package ch.sbb.workflow.controller;

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
import ch.sbb.atlas.api.workflow.WorkflowStartModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.workflow.WorkflowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(topics = {"atlas.mail"})
 class WorkflowControllerTest extends BaseControllerApiTest {

   static final String MAIL_ADDRESS = "marek@hamsik.com";
  @Autowired
  private WorkflowController controller;

  @Autowired
  private WorkflowRepository workflowRepository;

  @MockBean
  private LineWorkflowClient lineWorkflowClient;

  @BeforeEach
  void setUp() {
    when(lineWorkflowClient.processWorkflow(any())).thenReturn(WorkflowStatus.STARTED);
  }

  @AfterEach
   void tearDown() {
    workflowRepository.deleteAll();
  }

  @Test
   void shouldGetWorkflows() throws Exception {
    ClientPersonModel person = ClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    WorkflowStartModel workflowModel = WorkflowStartModel.builder()
        .client(person)
        .swissId("CH123456")
        .description("ch:123:431")
        .workflowComment("comment")
        .workflowType(WorkflowType.LINE)
        .businessObjectId(123456L)
        .build();

    controller.startWorkflow(workflowModel);

    mvc.perform(get("/v1/workflows"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
   void shouldGetWorkflowById() throws Exception {
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    Workflow workflow = Workflow.builder()
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
        .build();

    Workflow entity = workflowRepository.save(workflow);

    mvc.perform(get("/v1/workflows/" + entity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.swissId", is("CH123456")));
  }

  @Test
   void shouldCreateWorkflow() throws Exception {
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
        .build();

    //given
    mvc.perform(post("/v1/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated());
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
    mvc.perform(post("/v1/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value null rejected due to must not be null")))
        .andExpect(jsonPath("$.details[0].field", is("workflowType")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("null")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must not be null")));
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
    mvc.perform(post("/v1/workflows")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value \uD83D\uDE00\uD83D\uDE01? rejected due to must match "
            + "\"[\\u0000-\\u00ff]*\"")))
        .andExpect(jsonPath("$.details[0].field", is("client.firstName")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("\uD83D\uDE00\uD83D\uDE01?")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must match \"[\\u0000-\\u00ff]*\"")));
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
    mvc.perform(post("/v1/workflows")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(workflowModel))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
            .andExpect(jsonPath("$.error", is("Method argument not valid error")))
            .andExpect(jsonPath("$.details[0].message", is("Value \uD83D\uDE00\uD83D\uDE01? rejected due to must match "
                    + "\"[\\u0000-\\u00ff]*\"")))
            .andExpect(jsonPath("$.details[0].field", is("description")))
            .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
            .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
            .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("\uD83D\uDE00\uD83D\uDE01?")))
            .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
            .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must match \"[\\u0000-\\u00ff]*\"")));
  }

  @Test
   void shouldAcceptWorkflow() throws Exception {
    //when
    ClientPersonModel client = ClientPersonModel.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .personFunction("Centrocampista")
            .mail(MAIL_ADDRESS)
            .build();
    WorkflowStartModel workflowModel = WorkflowStartModel.builder()
            .client(client)
            .swissId("CH123456")
            .description("ch:123:431")
            .workflowComment("comment")
            .workflowType(WorkflowType.LINE)
            .businessObjectId(123456L)
            .build();
    WorkflowModel startedWorkflow = controller.startWorkflow(workflowModel);

    ExaminantWorkflowCheckModel workflowCheck = ExaminantWorkflowCheckModel.builder()
            .accepted(true).checkComment("ok").examinant(PersonModel.builder()
                    .firstName("Marek")
                    .lastName("Hamsik")
                    .personFunction("Centrocampista")
                    .build())
            .build();

    //given
    mvc.perform(post("/v1/workflows/" + startedWorkflow.getId() + "/examinant-check")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(workflowCheck)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.checkComment", is("ok")));
  }
}