package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

public final class ExportFilePath {

  private static final String PATH_DELIMITER = "/";
  private static final String FILENAME_DELIMITER = "-";

  private final LocalDate actualDate;
  private final String systemDir;
  private final String baseDir;
  private final String dir;
  @Getter
  private final String prefix;
  private final String fileName;
  private final String extension;

  public ExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName) {
    this(exportTypeBase, exportFileName, null, null);
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
    this.extension = exportExtensionFileType != null ? exportExtensionFileType.getExtension() : null;
    this.actualDate = actualDate;
  }

  public String fileName() {
    return dir + FILENAME_DELIMITER + prefixPathPart() + fileName + FILENAME_DELIMITER + actualDateString();
  }

  private String prefixPathPart() {
    if (prefix == null || prefix.isEmpty()) {
      return "";
    }
    return prefix + FILENAME_DELIMITER;
  }

  public String fileToStream() {
    return s3BucketDirPath() + PATH_DELIMITER + fileName() + ".json.gz";
  }

  public String actualDateFilePath() {
    return systemDir + fileName() + extension;
  }

  public String s3BucketDirPath() {
    return baseDir + PATH_DELIMITER + dir;
  }

  private String actualDateString() {
    return actualDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }

}
