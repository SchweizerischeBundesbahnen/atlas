package ch.sbb.importservice.service.csv;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
//TODO: add tests
@Data
@Builder
public class CsvFileNameModel {

  public static final String SERVICEPOINT_DIDOK_DIR_NAME = "servicepoint_didok";
  public static final String PRM_DIR_NAME = "opendata_didok";

  private final String fileName;
  private final String s3BucketDir;
  private final boolean addDateToPostfix;

  private String getFileNameWithTodayDate(String csvImportFilePrefix) {
    LocalDate today = LocalDate.now();
    return csvImportFilePrefix + replaceHyphensWithUnderscores(today.toString());
  }

  private String replaceHyphensWithUnderscores(String input) {
    return input.replace("-", "");
  }

  public String getFileName(){
    if(addDateToPostfix){
      return getFileNameWithTodayDate(this.fileName);
    }
    return this.fileName;
  }

}
