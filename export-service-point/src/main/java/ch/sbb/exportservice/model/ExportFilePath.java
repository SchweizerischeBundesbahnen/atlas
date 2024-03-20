package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

public final class ExportFilePath {

  private final LocalDate actualDate;
  private final String systemDir;
  private final String baseDir;
  private final String dir;
  @Getter
  private final String prefix;
  private final String fileName;
  private final String extension;

  public ExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName) {
    this(exportTypeBase, exportFileName, "", ExportExtensionFileType.CSV_EXTENSION);
  }

  public ExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName, String systemDir,
      ExportExtensionFileType exportExtensionFileType) {
    this(exportTypeBase, exportFileName, systemDir, exportExtensionFileType, LocalDate.now());
  }

  public ExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName, String systemDir,
      ExportExtensionFileType exportExtensionFileType, LocalDate actualDate) {
    this.baseDir = exportFileName.getBaseDir();
    this.dir = exportTypeBase.getDir();
    this.fileName = exportFileName.getFileName();
    this.prefix = exportTypeBase.getFileTypePrefix();

    this.systemDir = systemDir;
    this.extension = exportExtensionFileType.getExtension();
    this.actualDate = actualDate;
  }

  public String actualDateFileName() {
    if (prefix == null || prefix.isEmpty()) {
      return dir + "-" + fileName + "-" + actualDate();
    }
    return dir + "-" + prefix + "-" + fileName + "-" + actualDate();
  }

  public String getFileToStream() {
    return baseDir + "/" + dir + "/" + actualDateFileName() + ".json.gz";
  }

  public String actualDateFilePath() {
    return systemDir + actualDateFileName() + extension;
  }

  public String s3BucketDirPath() {
    return baseDir + "/" + dir;
  }

  private String actualDate() {
    return actualDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
