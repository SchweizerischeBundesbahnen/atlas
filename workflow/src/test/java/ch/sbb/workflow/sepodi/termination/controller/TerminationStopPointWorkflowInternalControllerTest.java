package ch.sbb.workflow.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationInfoModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointNotificationService;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointWorkflowService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class TerminationStopPointWorkflowInternalControllerTest extends BaseControllerApiTest {

  @Autowired
  private TerminationStopPointWorkflowInternalController controller;

  @Autowired
  private TerminationStopPointWorkflowService service;

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @Autowired
  private TerminationStopPointFeatureTogglingService featureTogglingService;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
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
        .build();

    when(sePoDiAdminClient.postStartServicePointTermination(eq(workflowModel.getSloid()), eq(workflowModel.getVersionId()), any(
        UpdateTerminationServicePointModel.class))).thenReturn(servicePointVersionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/internal/termination-stop-point/workflows")
        .contentType(contentType)
        .content(mapper.writeValueAsString(workflowModel))
    ).andExpect(status().isCreated()).andReturn();

    //then
    TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationStopPointWorkflowModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getBoTerminationDate()).isEqualTo(workflowModel.getBoTerminationDate());
    assertThat(result.getSloid()).isEqualTo(workflowModel.getSloid());
    verify(notificationService, times(1)).sendStartTerminationNotificationToInfoPlus(any(TerminationStopPointWorkflow.class));
    verify(notificationService, times(1)).sendStartConfirmationTerminationNotificationToApplicantMail(
        any(TerminationStopPointWorkflow.class));

  }

  @Test
  void shouldGetTerminationInfo() throws Exception {
    //given
    TerminationStopPointWorkflow workflow = TerminationStopPointWorkflow.builder()
        .boTerminationDate(LocalDate.of(2000, 12, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 12, 1))
        .novaTerminationDate(LocalDate.of(2000, 12, 1))
        .applicantMail("applicant@example.com")
        .sloid("ch:1:sloid:7000")
        .versionId(13L)
        .workflowComment("workflow comment")
        .status(TerminationWorkflowStatus.STARTED)
        .designationOfficial("official")
        .sboid("ch:1:sboid:132")
        .build();
    repository.save(workflow);

    //when
    MvcResult mvcResult = mvc.perform(get("/internal/termination-stop-point/workflows/termination-info/" + workflow.getSloid())
    ).andExpect(status().isOk()).andReturn();

    //then
    TerminationInfoModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationInfoModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isEqualTo(workflow.getBoTerminationDate());
    assertThat(result.getWorkflowId()).isNotNull();

  }

}