package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import java.io.IOException;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ExportTypeBase exportType, ExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }

  @Override
  protected void putFile() throws IOException {
    amazonService.putGzipFile(AmazonBucket.EXPORT, file(), exportFilePath.s3BucketDirPath());
  }
}
