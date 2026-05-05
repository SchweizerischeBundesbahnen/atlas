package ch.sbb.workflow.module.sepodi.termination.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class TerminationStopPointWorkflowApiInternalControllerTest extends BaseControllerApiTest {

  @Autowired
  private TerminationStopPointWorkflowRepository repository;

  @Autowired
  private PermissionRepository permissionRepository;

  @MockitoBean
  private TerminationStopPointNotificationService notificationService;

  @MockitoBean
  private SePoDiAdminClient sePoDiAdminClient;

  @AfterEach
  void tearDown() {
    repository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET internal/termination-stop-point/workflows/termination-info/{sloid}")
  class GetTerminationInfoBySloid {

    private final LocalDate boTerminationDate = LocalDate.of(2000, 12, 1);

    @Test
    void shouldGetTerminationInfo() throws Exception {
      //when
      MvcResult mvcResult = getTerminationInfo().andExpect(status().isOk()).andReturn();

      //then
      TerminationInfoModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
          TerminationInfoModel.class);
      assertThat(result).isNotNull();
      assertThat(result.getTerminationDate()).isEqualTo(boTerminationDate);
      assertThat(result.getWorkflowId()).isNotNull();
    }

    private ResultActions getTerminationInfo() throws Exception {
      //given
      TerminationStopPointWorkflow workflow = TerminationStopPointWorkflow.builder()
          .boTerminationDate(boTerminationDate)
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
      return mvc.perform(
          get(TerminationStopPointWorkflowApiInternal.BASE_PATH + "/termination-info/" + workflow.getSloid())
      );
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetTerminationInfoAsUnauthorized() throws Exception {
      getTerminationInfo().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("GET internal/termination-stop-point/workflows/abort/{workflowId}")
  class AbortTermination {

    @Test
    void shouldAbortTermination() throws Exception {
      MvcResult mvcResult = abortTermination().andExpect(status().isOk()).andReturn();

      //then
      TerminationStopPointWorkflowModel result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
          TerminationStopPointWorkflowModel.class);
      assertThat(result).isNotNull();
      assertThat(result.getStatus()).isEqualTo(TerminationWorkflowStatus.CANCELED);
    }

    private ResultActions abortTermination() throws Exception {
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
      return mvc.perform(post(TerminationStopPointWorkflowApiInternal.BASE_PATH + "/abort/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(abortComment)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAbortTerminationAsUnauthorized() throws Exception {
      abortTermination().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotAbortTerminationAsStandardUser() throws Exception {
      abortTermination().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAbortTerminationAsSupervisor() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.SEPODI)
          .role(ApplicationRole.SUPERVISOR)
          .build();
      permissionRepository.saveAndFlush(permission);

      abortTermination().andExpect(status().isOk());
    }
  }

}