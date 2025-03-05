package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Builder
public final class ExportFilePathV2 {

  private static final String PATH_DELIMITER = "/";
  private static final String FILENAME_DELIMITER = "-";

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

  public static ExportFilePathV2 buildV2(ExportObjectV2 type, ExportTypeV2 subtype) {
    return getV2Builder(type, subtype).build();
  }

  public static ExportFilePathV2Builder getV2Builder(ExportObjectV2 type, ExportTypeV2 subtype) {
    final String dir = switch (subtype) {
      case ACTUAL, SWISS_ACTUAL, WORLD_ACTUAL -> "actual-date";
      case FULL, SWISS_FULL, WORLD_FULL -> "full";
      case FUTURE_TIMETABLE, SWISS_FUTURE_TIMETABLE, WORLD_FUTURE_TIMETABLE -> "future-timetable";
    };

    final String prefix = switch (subtype) {
      case SWISS_FULL, SWISS_ACTUAL, SWISS_FUTURE_TIMETABLE -> "swiss-only";
      case WORLD_FULL, WORLD_ACTUAL, WORLD_FUTURE_TIMETABLE -> "world";
      default -> "";
    };

    ExportFilePathV2Builder exportFilePathBuilder = new ExportFilePathV2Builder();
    exportFilePathBuilder.actualDate(LocalDate.now());
    exportFilePathBuilder.baseDir("v2" + PATH_DELIMITER + type.name().toLowerCase().replace('_', '-'));
    exportFilePathBuilder.dir(dir);
    exportFilePathBuilder.prefix(prefix);
    exportFilePathBuilder.fileName(type.name().toLowerCase().replace('_', '-'));
    return exportFilePathBuilder;
  }

}
