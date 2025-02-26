package ch.sbb.exportservice.tasklet.upload;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.exportservice.model.ExportFilePathV2;
import java.io.IOException;

public class UploadCsvFileTaskletV2 extends FileUploadTaskletV2 {

  public UploadCsvFileTaskletV2(ExportFilePathV2 filePath) {
    super(filePath);
  }

  @Override
  protected void putFile() throws IOException {
    amazonService.putZipFileCleanupZip(AmazonBucket.EXPORT, file(), filePath.s3BucketDirPath());
  }

}
