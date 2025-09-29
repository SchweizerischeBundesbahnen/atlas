package ch.sbb.workflow.module.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.workflow.module.sepodi.client.SePoDiAdminClient;
import ch.sbb.workflow.module.sepodi.termination.api.TerminationStopPointWorkflowApiInternal;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationInfoModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import ch.sbb.workflow.module.sepodi.termination.service.TerminationStopPointNotificationService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class TerminationStopPointWorkflowApiInternalControllerTest extends BaseControllerApiTest {

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
  void shouldGetTerminationInfo() throws Exception {
    //given
    TerminationStopPointWorkflow workflow = TerminationStopPointWorkflow.builder()
        .boTerminationDate(LocalDate.of(2000, 12, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 12, 1))
        .infoPlusDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).build())
        .novaTerminationDate(LocalDate.of(2000, 12, 1))
        .novaDecision(TerminationDecision.builder().terminationDecisionPerson(TerminationDecisionPerson.NOVA).build())
        .applicantMail("applicant@example.com")
        .sloid("ch:1:sloid:7000")
        .versionId(13L)
        .workflowComment("workflow comment")
        .status(TerminationWorkflowStatus.STARTED)
        .designationOfficial("official")
        .versionValidTo(LocalDate.of(2000, 12, 31))
        .sboid("ch:1:sboid:132")
        .build();
    repository.save(workflow);

    //when
    MvcResult mvcResult = mvc.perform(
        get(TerminationStopPointWorkflowApiInternal.BASE_PATH + "/termination-info/" + workflow.getSloid())
    ).andExpect(status().isOk()).andReturn();

    //then
    TerminationInfoModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationInfoModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isEqualTo(workflow.getBoTerminationDate());
    assertThat(result.getWorkflowId()).isNotNull();

  }

  @Test
  void shouldAbortTermination() throws Exception {
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
    TerminationStopPointWorkflow stopPointWorkflow = repository.saveAndFlush(workflow);
    TerminationAbortModel abortComment = TerminationAbortModel.builder().abortComment("abortComment").build();

    //when
    MvcResult mvcResult =
        mvc.perform(post(TerminationStopPointWorkflowApiInternal.BASE_PATH + "/abort/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(abortComment))
        ).andExpect(status().isOk()).andReturn();

    //then
    TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
        TerminationStopPointWorkflowModel.class);
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.CANCELED);
  }

}