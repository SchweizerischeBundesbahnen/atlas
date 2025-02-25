package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public final class ExportFilePath {

  private static final String PATH_DELIMITER = "/";
  private static final String FILENAME_DELIMITER = "-";

  private final LocalDate actualDate;
  @Setter
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

  public static ExportFilePath buildV1(ExportObjectV1 type, ExportTypeV1 subtype) {
    return getV1Builder(type, subtype).build();
  }

  public static ExportFilePathBuilder getV1Builder(ExportObjectV1 type, ExportTypeV1 subtype) {
    final String dir = switch (subtype) {
      case ACTUAL -> switch (type) {
        case CONTACT_POINT_VERSION, PARKING_LOT_VERSION, PLATFORM_VERSION, REFERENCE_POINT_VERSION, RELATION_VERSION,
             STOP_POINT_VERSION, TOILET_VERSION -> "actual-date";
        case LOADING_POINT_VERSION, SERVICE_POINT_VERSION, TRAFFIC_POINT_ELEMENT_VERSION -> "actual_date";
      };

      case FULL, SWISS_ONLY_FULL, WORLD_FULL -> "full";
      case TIMETABLE_FUTURE -> switch (type) {
        case CONTACT_POINT_VERSION, PARKING_LOT_VERSION, PLATFORM_VERSION, REFERENCE_POINT_VERSION, RELATION_VERSION,
             STOP_POINT_VERSION, TOILET_VERSION -> "future-timetable";
        case LOADING_POINT_VERSION, SERVICE_POINT_VERSION, TRAFFIC_POINT_ELEMENT_VERSION -> "future_timetable";
      };

      case SWISS_ONLY_ACTUAL, WORLD_ONLY_ACTUAL -> "actual_date";
      case SWISS_ONLY_TIMETABLE_FUTURE, WORLD_ONLY_TIMETABLE_FUTURE -> "future_timetable";
    };

    final String prefix = switch (subtype) {
      case SWISS_ONLY_FULL, SWISS_ONLY_ACTUAL, SWISS_ONLY_TIMETABLE_FUTURE -> "swiss-only";
      case WORLD_FULL, WORLD_ONLY_ACTUAL, WORLD_ONLY_TIMETABLE_FUTURE -> "world";
      default -> "";
    };

    final String baseDir = switch (type) {
      case PLATFORM_VERSION, TOILET_VERSION, RELATION_VERSION -> {
        int i = type.name().indexOf('_');
        yield type.name().substring(0, i).toLowerCase();
      }
      default -> {
        int i = type.name().indexOf("_", type.name().indexOf('_') + 1);
        yield type.name().substring(0, i).toLowerCase();
      }
    };

    ExportFilePathBuilder exportFilePathBuilder = new ExportFilePathBuilder();
    exportFilePathBuilder.actualDate(LocalDate.now());
    exportFilePathBuilder.baseDir(baseDir);
    exportFilePathBuilder.dir(dir);
    exportFilePathBuilder.prefix(prefix);
    exportFilePathBuilder.fileName(type.name().toLowerCase());

    return exportFilePathBuilder;
  }

  public static ExportFilePath buildV2(ExportObject type, ExportType subtype) {
    return getV2Builder(type, subtype).build();
  }

  public static ExportFilePathBuilder getV2Builder(ExportObject type, ExportType subtype) {
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

    ExportFilePathBuilder exportFilePathBuilder = new ExportFilePathBuilder();
    exportFilePathBuilder.actualDate(LocalDate.now());
    exportFilePathBuilder.baseDir("v2" + PATH_DELIMITER + type.name().toLowerCase().replace('_', '-'));
    exportFilePathBuilder.dir(dir);
    exportFilePathBuilder.prefix(prefix);
    exportFilePathBuilder.fileName(type.name().toLowerCase().replace('_', '-'));

    return exportFilePathBuilder;
  }

}
