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
import ch.sbb.workflow.module.sepodi.client.SePoDiAdminClient;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class TerminationStopPointWorkflowApiV1ControllerTest extends BaseControllerApiTest {

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void shouldReturnFilteredSortedPagedListOfWorkflows() throws Exception {
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
    mvc.perform(get(TerminationStopPointWorkflowApiV1.BASE_PATH
            + "?searchCriterias=bern"
            + "&searchCriterias=ch:1:sloid:1"
            + "&workflowIds=" + savedWorkflowOne.getId()
            + "&status=TERMINATION_APPROVED"
            + "&status=STARTED"
            + "&sboids=ch:1:sboid:1"
            + "&sboids=ch:1:sboid:100157"
            + "&page=0&size=10&sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldSaveTerminationStopPoint() throws Exception {
    //given
    StartTerminationStopPointWorkflowModel workflowModel = StartTerminationStopPointWorkflowModel.builder()
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

    when(sePoDiAdminClient.startServicePointTermination(eq(workflowModel.getSloid()), eq(workflowModel.getVersionId()), any(
        UpdateTerminationServicePointModel.class))).thenReturn(servicePointVersionModel);

    //when
    MvcResult mvcResult = mvc.perform(post(TerminationStopPointWorkflowApiV1.BASE_PATH)
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated()).andReturn();

    //then
    TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationStopPointWorkflowModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getBoTerminationDate()).isEqualTo(workflowModel.getBoTerminationDate());
    assertThat(result.getSloid()).isEqualTo(workflowModel.getSloid());
    verify(notificationService, times(1)).sendStartTerminationNotificationToInfoPlusAndBo(
        any(TerminationStopPointWorkflow.class));

  }

  @Test
  void shouldGetTerminationStopPointById() throws Exception {
    //given
    TerminationStopPointWorkflow workflow = TerminationStopPointWorkflow.builder()
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
        .build();

    final TerminationStopPointWorkflow savedWorkflowOne = repository.save(workflow);

    //when
    MvcResult mvcResult =
        mvc.perform(get(TerminationStopPointWorkflowApiV1.BASE_PATH + "/" + savedWorkflowOne.getId()))
            .andExpect(status().isOk()).andReturn();

    //then
    TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationStopPointWorkflowModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getBoTerminationDate()).isEqualTo(workflow.getBoTerminationDate());
    assertThat(result.getSloid()).isEqualTo(workflow.getSloid());

  }

}