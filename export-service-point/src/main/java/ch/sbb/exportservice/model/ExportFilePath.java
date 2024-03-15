package ch.sbb.exportservice.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Builder
public class ExportFilePath {

  private LocalDate actualDate;
  private String systemDir;
  private String baseDir;
  private String dir;
  @Getter
  private String prefix;
  private String fileName;
  private String extension;

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
