package ch.sbb.importservice.listener;

import ch.sbb.atlas.imports.servicepoint.model.ItemImportResponseStatus;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.importservice.entity.ImportProcessItem;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.MailNotificationService;
import ch.sbb.importservice.service.MailProducerService;
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
  private final ImportProcessedItemRepository importProcessedItemRepository;
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
      clearDBFromSuccessImportedItem(stepExecution);
    }
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
      sendUnsuccessffulyNotification(stepExecution);
      importProcessedItemRepository.deleteAllByStepExecutionId(stepExecution.getId());
    }
  }

  private void sendUnsuccessffulyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    MailNotification mailNotification = mailNotificationService.buildMailErrorNotification(jobName, stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void sendSuccessfullyNotification(StepExecution stepExecution) {
    String jobName = getJobName(stepExecution);
    List<ImportProcessItem> allImportProcessedItem =
        importProcessedItemRepository.findAllByStepExecutionId(stepExecution.getId());

    MailNotification mailNotification = mailNotificationService.buildMailSuccessNotification(jobName, allImportProcessedItem,
        stepExecution);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void clearDBFromSuccessImportedItem(StepExecution stepExecution) {
    log.info("Deleting item processed from execution: {} ", stepExecution);
    importProcessedItemRepository.deleteAllByStepExecutionIdAndResponseStatus(stepExecution.getId(),
        ItemImportResponseStatus.SUCCESS);
  }

  private String getJobName(StepExecution stepExecution) {
    return stepExecution.getJobExecution().getJobInstance().getJobName();
  }

}
