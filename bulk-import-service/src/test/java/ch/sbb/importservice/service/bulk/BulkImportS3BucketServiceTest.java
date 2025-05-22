package ch.sbb.importservice.service.bulk;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.entity.BulkImport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BulkImportS3BucketServiceTest {

  @Mock
  private AmazonService amazonService;

  private BulkImportS3BucketService bulkImportS3BucketService;

  private final String todaysDir = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now());

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    bulkImportS3BucketService = new BulkImportS3BucketService(amazonService);
  }

  @Test
  void shouldUploadImportFileCorrectly() throws IOException {
    File file = Files.createTempFile("tmp", ".csv").toFile();
    bulkImportS3BucketService.uploadImportFile(file, BulkImport.builder()
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.UPDATE)
        .creator("e524381")
        .build());

    verify(amazonService).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class),
        eq("e524381/" + todaysDir + "/SEPODI/SERVICE_POINT/UPDATE"));
  }

  @Test
  void shouldDownloadCorrectly() {
    String filePath = "e524381/" + todaysDir + "/SEPODI/SERVICE_POINT/UPDATE";
    bulkImportS3BucketService.downloadImportFile(filePath);
    verify(amazonService).pullFile(AmazonBucket.BULK_IMPORT, filePath);
  }
}
