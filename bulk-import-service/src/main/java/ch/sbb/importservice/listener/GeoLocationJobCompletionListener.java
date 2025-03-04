package ch.sbb.importservice.listener;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.importservice.entity.GeoUpdateProcessItem;
import ch.sbb.importservice.repository.GeoUpdateProcessItemRepository;
import ch.sbb.importservice.service.mail.GeoLocationMailNotificationService;
import ch.sbb.importservice.service.mail.MailProducerService;
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
public class GeoLocationJobCompletionListener implements JobExecutionListener {

  private final GeoLocationMailNotificationService geoLocationMailNotificationService;
  private final GeoUpdateProcessItemRepository geoUpdateProcessItemRepository;
  private final MailProducerService mailProducerService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    //nothing to do
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    jobExecution.getStepExecutions().stream()
        .findFirst()
        .ifPresent(stepExecution -> {
          if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
            sendSuccessfullyNotification(stepExecution);
          }
          if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
            sendUnsuccessfullyNotification(stepExecution);
          }
          geoUpdateProcessItemRepository.deleteAllByStepExecutionId(stepExecution.getId());
        });

  }

  private void sendUnsuccessfullyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    MailNotification mailNotification = geoLocationMailNotificationService.buildMailErrorNotification(jobName, stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void sendSuccessfullyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    List<GeoUpdateProcessItem> allImportProcessedItem =
        geoUpdateProcessItemRepository.findAllByStepExecutionId(stepExecution.getId());

    MailNotification mailNotification = geoLocationMailNotificationService.buildMailSuccessNotification(jobName,
        allImportProcessedItem,
        stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private String getJobName(StepExecution stepExecution) {
    return stepExecution.getJobExecution().getJobInstance().getJobName();
  }

}
