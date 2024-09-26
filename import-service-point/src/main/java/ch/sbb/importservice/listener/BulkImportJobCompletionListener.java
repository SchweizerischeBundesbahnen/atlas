package ch.sbb.importservice.listener;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_ID_JOB_PARAMETER;

import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.bulk.BulkImportS3BucketService;
import ch.sbb.importservice.service.bulk.log.BulkImportLogService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.mail.MailProducerService;
import ch.sbb.importservice.utils.Translation;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BulkImportJobCompletionListener implements JobExecutionListener {

  private final BulkImportLogService bulkImportLogService;
  private final BulkImportS3BucketService s3BucketService;
  private final BulkImportRepository bulkImportRepository;
  private final MailProducerService mailProducerService;
  private final UserAdministrationClient userAdministrationClient;

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

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
    MailNotification mailNotification = MailNotification.builder()
        .to(List.of(userAdministrationClient.getCurrentUser().getMail()))
        .subject("Import Result " + bulkImport.getId())
        .mailType(MailType.BULK_IMPORT_RESULT_NOTIFICATION)
        .templateProperties(List.of(
            Map.of(
                "url", AtlasFrontendBaseUrl.getUrl(activeProfile) + "bulk-import/" + bulkImport.getId(),
                "applicationTypeDe", Translation.of(bulkImport.getApplication()).getDe(),
                "applicationTypeFr", Translation.of(bulkImport.getApplication()).getFr(),
                "applicationTypeIt", Translation.of(bulkImport.getApplication()).getIt(),
                "objectTypeDe", Translation.of(bulkImport.getObjectType()).getDe(),
                "objectTypeFr", Translation.of(bulkImport.getObjectType()).getFr(),
                "objectTypeIt", Translation.of(bulkImport.getObjectType()).getIt(),
                "importTypeDe", Translation.of(bulkImport.getImportType()).getDe(),
                "importTypeFr", Translation.of(bulkImport.getImportType()).getFr(),
                "importTypeIt", Translation.of(bulkImport.getImportType()).getIt()
            )
        ))
        .build();
    mailProducerService.produceMailNotification(mailNotification);
  }

}
