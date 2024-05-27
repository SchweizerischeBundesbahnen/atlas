package ch.sbb.workflow.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.api.workflow.StopPointAddWorkflowModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.client.SePoDiClient;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.workflow.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

class StopPointWorkflowControllerTest extends BaseControllerApiTest {

  static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowController controller;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @MockBean
  private SePoDiClient sePoDiClient;

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
    List<ClientPersonModel> clientPersonModels = new ArrayList<>();
    clientPersonModels.add(person);
    long versionId = 123456L;
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .swissMunicipalityName("Biel/Bienne")
        .swissCanton(SwissCanton.BERN)
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(clientPersonModels)
        .ccEmails(List.of("a@b.ch", "b@c.it"))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .versionId(versionId)
        .build();

    when(sePoDiClient.postServicePointsImport(versionId, Status.IN_REVIEW))
        .thenReturn(ResponseEntity.ok(getUpdateServicePointVersionModel(Status.IN_REVIEW)));

    controller.addWorkflow(workflowModel);

    mvc.perform(get("/v1/stop-point/workflows"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].examinants", hasSize(3)));
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
        .swissMunicipalityName("Biel/Bienne")
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
    ClientPersonModel person = ClientPersonModel.builder()
        .firstName("\uD83D\uDE00\uD83D\uDE01\uD83D")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .swissCanton(SwissCanton.BERN)
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .swissMunicipalityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(List.of(person))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
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
  void shouldCreateAddWorkflow() throws Exception {
    //when
    ClientPersonModel person = ClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    long versionId = 123456L;
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .swissCanton(SwissCanton.BERN)
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .swissMunicipalityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("WF comment")
        .examinants(List.of(person))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .versionId(versionId)
        .build();
    Mockito.when(sePoDiClient.postServicePointsImport(versionId, Status.IN_REVIEW))
        .thenReturn(ResponseEntity.ok(getUpdateServicePointVersionModel(Status.IN_REVIEW)));

    //given
    mvc.perform(post("/v1/stop-point/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated());
  }

  @Test
  void shouldNotCreateWorkflowWhenWorkflowWorkflowDescriptionHasWrongEncoding() throws Exception {
    //when
    ClientPersonModel person = ClientPersonModel.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .personFunction("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    StopPointAddWorkflowModel workflowModel = StopPointAddWorkflowModel.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .swissCanton(SwissCanton.BERN)
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .swissMunicipalityName("Biel/Bienne")
        .ccEmails(List.of(MAIL_ADDRESS))
        .workflowComment("\uD83D\uDE00\uD83D\uDE01\uD83D")
        .examinants(List.of(person))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
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

  private static UpdateServicePointVersionModel getUpdateServicePointVersionModel(Status status) {
    return UpdateServicePointVersionModel.builder()
        .designationLong("designation long 1")
        .designationOfficial("Aargau Strasse")
        .abbreviation("ABC")
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100871")
        .categories(List.of(Category.POINT_OF_SALE))
        .status(status)
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

}