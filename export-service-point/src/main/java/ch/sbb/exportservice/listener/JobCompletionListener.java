package ch.sbb.exportservice.listener;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.exportservice.service.MailNotificationService;
import ch.sbb.exportservice.service.MailProducerService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobCompletionListener implements JobExecutionListener {

  private final MailNotificationService mailNotificationService;
  private final MailProducerService mailProducerService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    //nothing to do
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    StepExecution stepExecution = jobExecution.getStepExecutions().stream().findFirst().get();
    if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
      sendSuccessfullyNotification(stepExecution);
    }
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
      sendUnsuccessffulyNotification(stepExecution);
    }
  }

  private void sendUnsuccessffulyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    MailNotification mailNotification = mailNotificationService.buildMailErrorNotification(jobName, stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void sendSuccessfullyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);

    //TODO
    List<String> exportedFiles = new ArrayList<>();
    MailNotification mailNotification = mailNotificationService.buildMailSuccessNotification(jobName, exportedFiles,
        stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private String getJobName(StepExecution stepExecution) {
    return stepExecution.getJobExecution().getJobInstance().getJobName();
  }

}
