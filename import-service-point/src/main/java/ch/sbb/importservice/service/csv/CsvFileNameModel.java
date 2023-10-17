package ch.sbb.importservice.service.csv;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CsvFileNameModel {

  private final String fileName;
  private final boolean addDateToPostfix;

  private String getFileNameWithTodayDate(String csvImportFilePrefix) {
    LocalDate today = LocalDate.now();
    return csvImportFilePrefix + replaceHyphensWithUnderscores(today.toString());
  }

  private String replaceHyphensWithUnderscores(String input) {
    return input.replaceAll("-", "");
  }

  public String getFileName(){
    if(addDateToPostfix){
      return getFileNameWithTodayDate(this.fileName);
    }
    return this.fileName;
  }

}
