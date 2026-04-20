package ch.sbb.line.directory.module.workflow.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.workflow.LineWorkflowApi;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.repository.LineVersionRepository;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

class LineWorkflowControllerTest extends BaseControllerApiTest {

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  private LineVersion lineVersion;

  @BeforeEach
  void setUp() {
    LineVersion entity = LineTestData.lineVersion();
    entity.setStatus(Status.DRAFT);
    lineVersion = lineVersionRepository.saveAndFlush(entity);
  }

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Test
  void shouldStartWorkflowAsAdmin() throws Exception {
    startWorkflow().andExpect(status().isOk());
  }

  private ResultActions startWorkflow() throws Exception {
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1L)
        .workflowType(WorkflowType.LINE)
        .businessObjectId(lineVersion.getId())
        .workflowStatus(WorkflowStatus.ADDED)
        .build();

    return mvc.perform(post("/" + LineWorkflowApi.BASEPATH + "process")
        .content(mapper.writeValueAsString(workflowEvent))
        .contentType(contentType));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldStartWorkflowAsWriter() throws Exception {
    Permission permission = Permission.builder()
        .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
        .application(ApplicationType.LIDI)
        .role(ApplicationRole.WRITER)
        .build();
    permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .permission(permission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction(lineVersion.getBusinessOrganisation())
        .build()));
    permissionRepository.saveAndFlush(permission);

    startWorkflow().andExpect(status().isOk());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldNotStartWorkflowAsUserWithoutPermissions() throws Exception {
    startWorkflow().andExpect(status().isForbidden());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldNotStartWorkflowAsUnauthorized() throws Exception {
    startWorkflow().andExpect(status().isForbidden());
  }
}