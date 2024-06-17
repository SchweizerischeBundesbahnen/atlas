package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@IntegrationTest
class StopPointWorkflowServiceTest {

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private StopPointWorkflowService workflowService;

  @BeforeEach
  void setUp() {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:8000")
        .sboid("ch:1:sboid:10")
        .status(WorkflowStatus.ADDED)
        .designationOfficial("Heimsiswil Zentrum")
        .versionId(1L)
        .localityName("Heimiswil")
        .build();
    workflowRepository.save(stopPointWorkflow);
  }

  @AfterEach
  void tearDown() {
    workflowRepository.deleteAll();
  }

  @Test
  void shouldFindWorkflowByLocalityCaseInsensitive() {
    List<StopPointWorkflow> searchResult = workflowService.getWorkflows(
        StopPointWorkflowSearchRestrictions.builder().pageable(Pageable.unpaged()).stopPointWorkflowRequestParams(
            StopPointWorkflowRequestParams.builder()
                .localityName("heimiswil")
                .build()).build()).getContent();

    assertThat(searchResult).hasSize(1);
  }

  @Test
  void shouldNotFindWorkflowByLocalityCaseInsensitive() {
    List<StopPointWorkflow> searchResult = workflowService.getWorkflows(
        StopPointWorkflowSearchRestrictions.builder().pageable(Pageable.unpaged()).stopPointWorkflowRequestParams(
            StopPointWorkflowRequestParams.builder()
                .localityName("notexisting")
                .build()).build()).getContent();

    assertThat(searchResult).isEmpty();
  }
}