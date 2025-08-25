package ch.sbb.line.directory.module.tth.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimetableHearingStatementCsvModelTest {

  @Test
  void shouldReturnOnlyCityWhenZipNull() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(null, "Bern");
    Assertions.assertEquals("Bern", zipAndCity);
  }

  @Test
  void shouldReturnOnlyZipWhenCityEmptyString() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(3005, "");
    Assertions.assertEquals("3005", zipAndCity);
  }

  @Test
  void shouldReturnOnlyZipWhenCityNull() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(3005, null);
    Assertions.assertEquals("3005", zipAndCity);
  }

  @Test
  void shouldReturnZipAndCityWhenBothGiven() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(3005, "Bern");
    Assertions.assertEquals("3005/Bern", zipAndCity);
  }

  @Test
  void shouldReturnEmptyStringWhenZipAndCityNull() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(null, null);
    Assertions.assertEquals("", zipAndCity);
  }

  @Test
  void shouldReturnEmptyStringWhenZipNullAndCityEmpty() {
    String zipAndCity = TimetableHearingStatementCsvModel.getZipAndCity(null, "");
    Assertions.assertEquals("", zipAndCity);
  }

}
