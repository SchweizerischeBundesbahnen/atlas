package ch.sbb.importservice.listener;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_ID_JOB_PARAMETER;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.bulk.BulkImportS3BucketService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.log.PersistedLogService;
import ch.sbb.importservice.service.mail.MailNotificationService;
import ch.sbb.importservice.service.mail.MailProducerService;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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
public class BulkImportJobCompletionListener implements JobExecutionListener {

  private final MailNotificationService mailNotificationService;
  private final MailProducerService mailProducerService;
  private final PersistedLogService persistedLogService;
  private final BulkImportS3BucketService s3BucketService;
  private final BulkImportRepository bulkImportRepository;

  @Override
  public void afterJob(JobExecution jobExecution) {
    LogFile logFile = persistedLogService.getLogFile(jobExecution.getId());
    log.info("Persisted log file: {}", logFile);

    Long bulkImportId = Objects.requireNonNull(jobExecution.getJobParameters().getLong(BULK_IMPORT_ID_JOB_PARAMETER));
    BulkImport currentImport = bulkImportRepository.findById(bulkImportId).orElseThrow();

    URL logUrl = s3BucketService.uploadImportFile(persistedLogService.writeLogToFile(logFile, currentImport), currentImport);
    currentImport.setLogFileUrl(logUrl.getPath().substring(1));
    bulkImportRepository.save(currentImport);

    Optional<StepExecution> stepExecution = jobExecution.getStepExecutions().stream().findFirst();
    if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus()) && stepExecution.isPresent()) {

    }
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus()) && stepExecution.isPresent()) {

    }
  }


}
