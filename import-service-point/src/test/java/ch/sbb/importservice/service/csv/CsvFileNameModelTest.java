package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;
import static ch.sbb.importservice.service.csv.StopPointCsvService.PRM_STOP_PLACES_FILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;

class CsvFileNameModelTest {

  @Test
  void shouldReturnFileNameWithDate(){
    //given
    CsvFileNameModel csvFileNameModel = CsvFileNameModel.builder()
        .fileName(PRM_STOP_PLACES_FILE_NAME)
        .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
        .addDateToPostfix(true)
        .build();
    //when
    String result = csvFileNameModel.getFileName();
    //then
    assertThat(result).isNotNull();
    String postfixDate = result.substring(result.lastIndexOf("_") + 1);
    assertThat(postfixDate).isEqualTo(csvFileNameModel.replaceHyphensWithUnderscores(LocalDate.now().toString()));
  }

  @Test
  void shouldReturnFileNameWithoutDate(){
    //given
    CsvFileNameModel csvFileNameModel = CsvFileNameModel.builder()
        .fileName(PRM_STOP_PLACES_FILE_NAME)
        .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
        .addDateToPostfix(false)
        .build();
    //when
    String result = csvFileNameModel.getFileName();
    //then
    assertThat(result).isNotNull().isEqualTo(PRM_STOP_PLACES_FILE_NAME);
  }

}