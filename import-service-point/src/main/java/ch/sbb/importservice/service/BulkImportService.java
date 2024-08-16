package ch.sbb.importservice.service;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import jakarta.transaction.Transactional;
import java.io.File;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BulkImportService {

  private final BulkImportRepository bulkImportRepository;
  private final BulkImportS3BucketService bulkImportS3BucketService;
  private final BulkImportJobService bulkImportJobService;

  @Async
  public void startBulkImport(BulkImport bulkImport, File file) {
    String s3ObjectKey = uploadImportFile(file, bulkImport);
    bulkImport.setImportFileUrl(s3ObjectKey);

    saveBulkImportMetaData(bulkImport);

    bulkImportJobService.startBulkImportJob(bulkImport, file);
  }

  private String uploadImportFile(File file, BulkImport bulkImport) {
    URL uploadedImportFileUrl = bulkImportS3BucketService.uploadImportFile(file, bulkImport);
    return uploadedImportFileUrl.getPath().substring(1);
  }

  public void saveBulkImportMetaData(BulkImport bulkImport) {
    bulkImportRepository.saveAndFlush(bulkImport);
  }
}
