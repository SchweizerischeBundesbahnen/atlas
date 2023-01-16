package ch.sbb.importservice.listener;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.importservice.entitiy.ImportProcessItem;
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
public class JobCompletitionListener implements JobExecutionListener {

  private final MailNotificationService mailNotificationService;
  private final ImportProcessedItemRepository importProcessedItemRepository;

  private final MailProducerService mailProducerService;

  @Override
  public void beforeJob(JobExecution jobExecution) {

  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    StepExecution stepExecution = jobExecution.getStepExecutions().stream().findFirst().get();
    if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
      sendSuccessfullyNotification(stepExecution);
      clearDB(jobExecution);
    }
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
      sendUnseccessufflyNotification(stepExecution);
    }

  }

  private void sendUnseccessufflyNotification(StepExecution stepExecution) {
    MailNotification mailNotification = mailNotificationService.buildMailErrorNotification("importDinstelle", stepExecution);
    mailProducerService.produceMailNotification(mailNotification);

  }

  private void sendSuccessfullyNotification(StepExecution stepExecution) {
    List<ImportProcessItem> allImportProcessedItem =
        importProcessedItemRepository.findAllByStepExecutionId(stepExecution.getId());
    MailNotification mailNotification = mailNotificationService.buildMailSuccessNotification("importDinstelle",
        allImportProcessedItem);
    mailProducerService.produceMailNotification(mailNotification);
  }

  private void clearDB(JobExecution jobExecution) {
    StepExecution execution = jobExecution.getStepExecutions().stream()
        .filter(stepExecution -> stepExecution.getStepName().equals("parseCsvStep")).findFirst()
        .orElseThrow(() -> new IllegalStateException("No Step Found with name [parseCsvStep]. Please check the job "
            + "configuration"));
    log.info("Deleating item processed from execution: {} ", execution);
    importProcessedItemRepository.deleteAllByStepExecutionId(execution.getId());
  }

}
