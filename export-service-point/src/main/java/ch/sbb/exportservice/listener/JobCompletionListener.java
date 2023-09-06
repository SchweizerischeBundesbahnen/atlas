package ch.sbb.exportservice.listener;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.exportservice.service.MailNotificationService;
import ch.sbb.exportservice.service.MailProducerService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobCompletionListener implements JobExecutionListener {

  private final MailNotificationService mailNotificationService;
  private final MailProducerService mailProducerService;

  @Override
  public void beforeJob(@NotNull JobExecution jobExecution) {
    // nothing to do
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
      Optional<StepExecution> failedStepExecution = jobExecution.getStepExecutions().stream()
          .filter(stepExecution -> stepExecution.getStatus() == BatchStatus.FAILED).findFirst();
      failedStepExecution.ifPresent(this::sendUnsuccessffulyNotification);
    }
  }

  private void sendUnsuccessffulyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    MailNotification mailNotification = mailNotificationService.buildMailErrorNotification(jobName, stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private String getJobName(StepExecution stepExecution) {
    return stepExecution.getJobExecution().getJobInstance().getJobName();
  }

}
