package ch.sbb.exportservice.model;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
public final class ExportFilePathV2 implements Serializable {

  private static final String FILENAME_DELIMITER = "-";
  public static final String XML_ZIP_EXTENSION = ".xml.zip";
  public static final String JSON_GZ_EXTENSION = ".json.gz";

  @Serial
  private static final long serialVersionUID = -857098055542197423L;

  private final LocalDate actualDate;
  private String systemDir;
  private final String baseDir;
  private final String dir;
  @Getter
  private final String prefix;
  private final String fileName;
  private final String extension;

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
    return String.join("/", s3BucketDirPath(), fileName() + JSON_GZ_EXTENSION);
  }

  public String xmlZipFileToStream() {
    return String.join("/", s3BucketDirPath(), fileName() + XML_ZIP_EXTENSION);
  }

  /**
   * systemDir is expected to have a trailing backslash
   */
  public String actualDateFilePath() {
    return systemDir + fileName() + extension;
  }

  public String s3BucketDirPath() {
    return String.join("/", baseDir, dir);
  }

  private String actualDateString() {
    return actualDate.format(DATE_FORMATTER_BASE);
  }

  public static ExportFilePathV2 buildV2(ExportObjectV2 type, ExportTypeV2 subtype) {
    return getV2Builder(type, subtype).build();
  }

  public static ExportFilePathV2Builder getV2Builder(ExportObjectV2 type, ExportTypeV2 subtype) {
    final ExportFilePathV2Builder exportFilePathBuilder = new ExportFilePathV2Builder();
    exportFilePathBuilder.actualDate(LocalDate.now());
    exportFilePathBuilder.baseDir(String.join("/", "v2", type.getName()));
    exportFilePathBuilder.dir(subtype.dir);
    exportFilePathBuilder.prefix(subtype.prefix);
    exportFilePathBuilder.fileName(type.getName());
    return exportFilePathBuilder;
  }

}
