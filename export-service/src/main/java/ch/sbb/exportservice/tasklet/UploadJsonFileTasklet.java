package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import java.io.IOException;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ExportFilePath.ExportFilePathBuilder systemFile,
      ExportFilePath.ExportFilePathBuilder s3File) {
    super(systemFile, s3File);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }

  @Override
  protected void putFile() throws IOException {
    amazonService.putGzipFile(AmazonBucket.EXPORT, file(), s3File.s3BucketDirPath());
  }
}
