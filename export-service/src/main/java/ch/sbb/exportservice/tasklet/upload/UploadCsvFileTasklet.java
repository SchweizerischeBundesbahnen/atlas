package ch.sbb.exportservice.tasklet.upload;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import java.io.IOException;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ExportTypeBase exportType, ExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

  @Override
  protected void putFile() throws IOException {
    amazonService.putZipFileCleanupBoth(AmazonBucket.EXPORT, file(), exportFilePath.s3BucketDirPath());
  }
}
