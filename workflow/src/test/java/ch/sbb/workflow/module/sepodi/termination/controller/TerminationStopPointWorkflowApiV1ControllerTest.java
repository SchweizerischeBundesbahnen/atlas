package ch.sbb.workflow.module.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.workflow.module.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.module.sepodi.client.SePoDiClient;
import ch.sbb.workflow.module.sepodi.termination.api.TerminationStopPointWorkflowApiV1;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import ch.sbb.workflow.module.sepodi.termination.service.TerminationStopPointNotificationService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class TerminationStopPointWorkflowApiV1ControllerTest extends BaseControllerApiTest {

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @MockitoBean
  private SePoDiClient sePoDiClient;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Nested
  @DisplayName("GET /v1/termination-stop-point/workflows")
  class GetTerminationStopPointWorkflows {

    @Test
    void shouldReturnFilteredSortedPagedListOfWorkflows() throws Exception {
      returnFilteredSortedPagedListOfWorkflows()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount", is(1)))
          .andExpect(jsonPath("$.objects", hasSize(1)))
          .andExpect(jsonPath("$.objects[0].applicantMail", is("secret-mail@sbb.ch")));
    }

    private ResultActions returnFilteredSortedPagedListOfWorkflows() throws Exception {
      // given
      TerminationStopPointWorkflow workflowOne = TerminationStopPointWorkflow.builder()
          .sboid("ch:1:sboid:1")
          .versionId(50L)
          .sloid("ch:1:sloid:1")
          .boTerminationDate(LocalDate.of(2000, 1, 1))
          .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
          .infoPlusDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).build())
          .novaTerminationDate(LocalDate.of(2000, 1, 3))
          .novaDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.NOVA).build())
          .designationOfficial("Bern")
          .versionValidTo(LocalDate.of(2000, 12, 31))
          .status(TerminationWorkflowStatus.STARTED)
          .applicantMail("secret-mail@sbb.ch")
          .build();
      TerminationStopPointWorkflow workflowTwo = TerminationStopPointWorkflow.builder()
          .sboid("ch:1:sboid:2")
          .versionId(55L)
          .sloid("ch:1:sloid:2")
          .boTerminationDate(LocalDate.of(2000, 1, 1))
          .infoPlusDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).build())
          .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
          .novaTerminationDate(LocalDate.of(2000, 1, 3))
          .novaDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.NOVA).build())
          .designationOfficial("Züri")
          .versionValidTo(LocalDate.of(2000, 12, 31))
          .status(TerminationWorkflowStatus.TERMINATION_APPROVED)
          .build();

      final TerminationStopPointWorkflow savedWorkflowOne = repository.save(workflowOne);
      repository.save(workflowTwo);

      // when & then
      return mvc.perform(get(TerminationStopPointWorkflowApiV1.BASE_PATH
          + "?searchCriterias=bern"
          + "&searchCriterias=ch:1:sloid:1"
          + "&workflowIds=" + savedWorkflowOne.getId()
          + "&status=TERMINATION_APPROVED"
          + "&status=STARTED"
          + "&sboids=ch:1:sboid:1"
          + "&sboids=ch:1:sboid:100157"
          + "&page=0&size=10&sort=id,desc"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldReturnFilteredSortedPagedListOfWorkflowsRedactedForUnauthorized() throws Exception {
      returnFilteredSortedPagedListOfWorkflows()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount", is(1)))
          .andExpect(jsonPath("$.objects", hasSize(1)))
          .andExpect(jsonPath("$.objects[0].applicantMail", is("s*****")));
    }
  }

  @Nested
  @DisplayName("GET /v1/termination-stop-point/workflows/{id}")
  class GetTerminationStopPointWorkflow {

    private TerminationStopPointWorkflow workflow;

    @Test
    void shouldGetTerminationStopPointById() throws Exception {
      //when
      MvcResult mvcResult = getTerminationStopPointById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.applicantMail", is("secret-mail@sbb.ch")))
          .andReturn();

      //then
      TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
          TerminationStopPointWorkflowModel.class);
      assertThat(result).isNotNull();
      assertThat(result.getBoTerminationDate()).isEqualTo(workflow.getBoTerminationDate());
      assertThat(result.getSloid()).isEqualTo(workflow.getSloid());
    }

    private ResultActions getTerminationStopPointById() throws Exception {
      //given
      workflow = TerminationStopPointWorkflow.builder()
          .sboid("ch:1:sboid:1")
          .versionId(50L)
          .sloid("ch:1:sloid:1")
          .boTerminationDate(LocalDate.of(2000, 1, 1))
          .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
          .infoPlusDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).build())
          .novaTerminationDate(LocalDate.of(2000, 1, 3))
          .novaDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.NOVA).build())
          .designationOfficial("Bern")
          .versionValidTo(LocalDate.of(2000, 12, 31))
          .status(TerminationWorkflowStatus.STARTED)
          .applicantMail("secret-mail@sbb.ch")
          .build();

      final TerminationStopPointWorkflow savedWorkflowOne = repository.save(workflow);

      //when
      return mvc.perform(get(TerminationStopPointWorkflowApiV1.BASE_PATH + "/" + savedWorkflowOne.getId()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetTerminationStopPointByIdRedactedAsUnauthorized() throws Exception {
      getTerminationStopPointById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.applicantMail", is("s*****")))
          .andReturn();
    }
  }

  @Nested
  @DisplayName("POST /v1/termination-stop-point/workflows")
  class StartTerminationStopPointWorkflow {

    private StartTerminationStopPointWorkflowModel workflowModel;

    @Test
    void shouldSaveTerminationStopPoint() throws Exception {
      //when
      MvcResult mvcResult = saveTerminationStopPoint().andExpect(status().isCreated()).andReturn();

      //then
      TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
          TerminationStopPointWorkflowModel.class);
      assertThat(result).isNotNull();
      assertThat(result.getBoTerminationDate()).isEqualTo(workflowModel.getBoTerminationDate());
      assertThat(result.getSloid()).isEqualTo(workflowModel.getSloid());
      verify(notificationService, times(1)).sendStartTerminationNotificationToInfoPlusAndBo(
          any(TerminationStopPointWorkflow.class));
    }

    private ResultActions saveTerminationStopPoint() throws Exception {
      //given
      workflowModel = StartTerminationStopPointWorkflowModel.builder()
          .boTerminationDate(LocalDate.of(2000, 12, 1))
          .applicantMail("applicant@example.com")
          .sloid("ch:1:sloid:7000")
          .versionId(13L)
          .workflowComment("workflow comment")
          .build();

      ReadServicePointVersionModel servicePointVersionModel = ReadServicePointVersionModel.builder()
          .designationOfficial("official")
          .businessOrganisation("ch:1:sboid:132")
          .validTo(LocalDate.of(2000, 12, 31))
          .build();

      when(sePoDiClient.startServicePointTermination(eq(workflowModel.getSloid()), eq(workflowModel.getVersionId()), any(
          UpdateTerminationServicePointModel.class))).thenReturn(servicePointVersionModel);

      //when
      return mvc.perform(post(TerminationStopPointWorkflowApiV1.BASE_PATH)
          .contentType(contentType)
          .content(mapper.writeValueAsString(workflowModel))
      );
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldSaveTerminationStopPointAsStandardUser() throws Exception {
      saveTerminationStopPoint().andExpect(status().isCreated());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotSaveTerminationStopPointAsUnauthorized() throws Exception {
      saveTerminationStopPoint().andExpect(status().isForbidden());
    }
  }

}