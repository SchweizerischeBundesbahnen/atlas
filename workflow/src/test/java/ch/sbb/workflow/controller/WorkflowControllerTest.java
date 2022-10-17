package ch.sbb.workflow.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import ch.sbb.workflow.api.PersonModel;
import ch.sbb.workflow.api.WorkflowModel;
import ch.sbb.workflow.entity.BusinessObjectType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.entity.WorkflowStatus;
import ch.sbb.workflow.entity.WorkflowType;
import ch.sbb.workflow.workflow.WorkflowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowControllerTest extends BaseControllerApiTest {

  @Autowired
  private WorkflowController controller;

  @Autowired
  private WorkflowRepository workflowRepository;

  @AfterEach
  public void tearDown() {
    workflowRepository.deleteAll();
  }

  @Test
  public void shouldGetWorkflows() throws Exception {
    PersonModel person = PersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.c").build();
    WorkflowModel workflowModel = WorkflowModel.builder()
        .client(person)
        .swissId("CH123456")
        .examinant(person)
        .workflowType(WorkflowType.LINE)
        .businessObjectId(123456L)
        .businessObjectType(BusinessObjectType.SLNID)
        .build();

    controller.createWorkflow(workflowModel);

    mvc.perform(get("/v1/workflows/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void shouldGetWorkflowById() throws Exception {
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.c").build();
    Workflow workflow = Workflow.builder()
        .client(person)
        .examinant(person)
        .swissId("CH123456")
        .status(WorkflowStatus.ADDED)
        .examinant(person)
        .workflowType(WorkflowType.LINE)
        .businessObjectId(123456L)
        .businessObjectType(BusinessObjectType.SLNID)
        .build();

    Workflow entity = workflowRepository.save(workflow);

    mvc.perform(get("/v1/workflows/" + entity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.swissId", is("CH123456")));
  }

  @Test
  public void shouldCreateWorkflow() throws Exception {
    //when
    PersonModel person = PersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.c").build();
    WorkflowModel workflowModel = WorkflowModel.builder()
        .client(person)
        .examinant(person)
        .swissId("CH123456")
        .examinant(person)
        .workflowType(WorkflowType.LINE)
        .businessObjectId(123456L)
        .businessObjectType(BusinessObjectType.SLNID)
        .build();

    //given
    mvc.perform(post("/v1/workflows/")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated());
  }

  @Test
  public void shouldNotCreateWorkflowWhenWorkflowTypeIsNull() throws Exception {
    //when
    PersonModel person = PersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.c").build();
    WorkflowModel workflowModel = WorkflowModel.builder()
        .client(person)
        .examinant(person)
        .swissId("CH123456")
        .examinant(person)
        .businessObjectId(123456L)
        .businessObjectType(BusinessObjectType.SLNID)
        .build();

    //given
    mvc.perform(post("/v1/workflows/")
            .contentType(contentType)
            .content(mapper.writeValueAsString(workflowModel))
        ).andDo(print())
        .andExpect(status().isBadRequest())
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

}