package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import java.io.IOException;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ExportFilePath.ExportFilePathBuilder systemFile, ExportFilePath.ExportFilePathBuilder s3File) {
    super(systemFile, s3File);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

  @Override
  protected void putFile() throws IOException {
    amazonService.putZipFileCleanupZip(AmazonBucket.EXPORT, file(), s3File.s3BucketDirPath());
  }
}
