package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.importservice.entity.BulkImport;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BulkImportS3BucketService {

  private final AmazonService amazonService;

  public URL uploadImportFile(File file, BulkImport bulkImport) {
    return amazonService.putFile(AmazonBucket.BULK_IMPORT, file, createImportFilePath(bulkImport));
  }

  public File downloadImportFile(String filePath) {
    return amazonService.pullFile(AmazonBucket.BULK_IMPORT, filePath);
  }

  /**
   * {user}/{date(without time)}/{application}/{objectType}/{importType}
   */
  String createImportFilePath(BulkImport bulkImport) {
    return String.format("%s/%s/%s/%s/%s",
        bulkImport.getCreator(),
        DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now()),
        bulkImport.getApplication(),
        bulkImport.getObjectType(),
        bulkImport.getImportType()
    );
  }

}
