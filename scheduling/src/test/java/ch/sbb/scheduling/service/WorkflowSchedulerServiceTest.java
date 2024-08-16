package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.scheduling.client.WorkflowClient;
import ch.sbb.scheduling.exception.SchedulingExecutionException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import feign.Util;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class WorkflowSchedulerServiceTest {

  private WorkflowSchedulerService workflowSchedulerService;

  @Mock
  private WorkflowClient workflowClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    workflowSchedulerService = new WorkflowSchedulerService(workflowClient);
  }

  @Test
  void shouldEndExpiredWorkflowsSuccessfully() {
    //given
    Response response = Response.builder()
        .status(200)
        .reason("OK")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(workflowClient.endExpiredWorkflows()).thenReturn(response);

    //when
    Response result = workflowSchedulerService.endExpiredWorkflows();

    //then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(200);
  }

  @Test
  void shouldEndExpiredWorkflowsUnsuccessful() {
    //given
    Response response = Response.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .reason("Bad Request")
        .request(
            Request.create(HttpMethod.POST, "/api", Collections.emptyMap(),
                null, Util.UTF_8, null))
        .build();
    when(workflowClient.endExpiredWorkflows()).thenReturn(response);

    //when
    assertThrows(SchedulingExecutionException.class,
        () -> workflowSchedulerService.endExpiredWorkflows());
  }
}