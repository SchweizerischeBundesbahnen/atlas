package ch.sbb.importservice.service.bulk;

import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
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

  private static final String ROOT_TEMPLATES_BUCKET_FOLDER = "templates";
  private static final String FILE_EXTENSION = ".xlsx";
  private static final String PATH_DELIMITER = "/";
  private static final String UNDERSCORE_DELIMITER = "_";

  @Async
  public void startBulkImport(BulkImport bulkImport, File file, List<String> emails) {
    String s3ObjectKey = uploadImportFile(file, bulkImport);
    bulkImport.setImportFileUrl(s3ObjectKey);

    BulkImport bulkImportData = saveBulkImportMetaData(bulkImport);
    bulkImportJobService.startBulkImportJob(bulkImportData, file);
  }

  public File downloadTemplate(BusinessObjectType objectType, ImportType importType) {
    String filePath = ROOT_TEMPLATES_BUCKET_FOLDER + PATH_DELIMITER + objectType.toString().toLowerCase() + PATH_DELIMITER
       + importType.toString().toLowerCase() + UNDERSCORE_DELIMITER + objectType.toString().toLowerCase() + FILE_EXTENSION;
    return bulkImportS3BucketService.downloadImportFile(filePath);
  }

  private String uploadImportFile(File file, BulkImport bulkImport) {
    URL uploadedImportFileUrl = bulkImportS3BucketService.uploadImportFile(file, bulkImport);
    return uploadedImportFileUrl.getPath().substring(1);
  }

  private BulkImport saveBulkImportMetaData(BulkImport bulkImport) {
    return bulkImportRepository.saveAndFlush(bulkImport);
  }

}
