package ch.sbb.importservice.listener;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_ID_JOB_PARAMETER;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.bulk.BulkImportS3BucketService;
import ch.sbb.importservice.service.bulk.log.BulkImportLogService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import java.net.URL;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BulkImportJobCompletionListener implements JobExecutionListener {

  private final BulkImportLogService bulkImportLogService;
  private final BulkImportS3BucketService s3BucketService;
  private final BulkImportRepository bulkImportRepository;

  @Override
  public void afterJob(JobExecution jobExecution) {
    LogFile logFile = bulkImportLogService.getLogFile(jobExecution.getId());
    Long bulkImportId = Objects.requireNonNull(jobExecution.getJobParameters().getLong(BULK_IMPORT_ID_JOB_PARAMETER));
    BulkImport currentImport = bulkImportRepository.findById(bulkImportId).orElseThrow();

    uploadLogFile(logFile, currentImport);
    bulkImportLogService.deleteLog(jobExecution.getId());

    sendMailToImporter(currentImport);
  }

  private void uploadLogFile(LogFile logFile, BulkImport bulkImport) {
    URL logUrl = s3BucketService.uploadImportFile(bulkImportLogService.writeLogToFile(logFile, bulkImport), bulkImport);
    bulkImport.setLogFileUrl(logUrl.getPath().substring(1));
    bulkImportRepository.save(bulkImport);
  }

  private void sendMailToImporter(BulkImport bulkImport) {
    // Send mail with link to atlas log gui
    
  }
}
