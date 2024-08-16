package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.WorkflowClient;
import ch.sbb.scheduling.exception.SchedulingExecutionException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkflowSchedulerService extends BaseSchedulerService {

  private final WorkflowClient workflowClient;

  public WorkflowSchedulerService(WorkflowClient workflowClient) {
    this.workflowClient = workflowClient;
    this.clientName = "Workflow-Client";
  }

  @SpanTracing
  @Retryable(label = "endExpiredWorkflows", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.workflow.end-expired-workflows.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "endExpiredWorkflows", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response endExpiredWorkflows() {
    return executeRequest(workflowClient::endExpiredWorkflows, "End Expired Workflows");
  }

}