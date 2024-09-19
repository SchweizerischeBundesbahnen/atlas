package ch.sbb.importservice.service.bulk;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import java.io.File;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportService {

  private final BulkImportRepository bulkImportRepository;
  private final BulkImportS3BucketService bulkImportS3BucketService;
  private final BulkImportJobService bulkImportJobService;

  @Async
  public void startBulkImport(BulkImport bulkImport, File file, List<String> emails) {
    String s3ObjectKey = uploadImportFile(file, bulkImport);
    bulkImport.setImportFileUrl(s3ObjectKey);

    BulkImport bulkImportData = saveBulkImportMetaData(bulkImport);
    bulkImportJobService.startBulkImportJob(bulkImportData, file);
  }

  private String uploadImportFile(File file, BulkImport bulkImport) {
    URL uploadedImportFileUrl = bulkImportS3BucketService.uploadImportFile(file, bulkImport);
    return uploadedImportFileUrl.getPath().substring(1);
  }

  private BulkImport saveBulkImportMetaData(BulkImport bulkImport) {
    return bulkImportRepository.saveAndFlush(bulkImport);
  }

}
