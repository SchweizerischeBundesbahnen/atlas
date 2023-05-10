package ch.sbb.line.directory.service;

import ch.sbb.line.directory.model.csv.TimetableHearingStatementCsvModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimetableHearingStatementCsvModelTest {

    @Test
    void shouldReturnOnlyCityWhenZipNull() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(null, "Bern");
        Assertions.assertEquals("Bern", zipAndCity);
    }

    @Test
    void shouldReturnOnlyZipWhenCityEmptyString() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(3005, "");
        Assertions.assertEquals("3005", zipAndCity);
    }

    @Test
    void shouldReturnOnlyZipWhenCityNull() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(3005, null);
        Assertions.assertEquals("3005", zipAndCity);
    }

    @Test
    void shouldReturnZipAndCityWhenBothGiven() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(3005, "Bern");
        Assertions.assertEquals("3005/Bern", zipAndCity);
    }

    @Test
    void shouldReturnEmptyStringWhenZipAndCityNull() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(null, null);
        Assertions.assertEquals("", zipAndCity);
    }

    @Test
    void shouldReturnEmptyStringWhenZipNullAndCityEmpty() {
        String zipAndCity = TimetableHearingStatementCsvModel.setZipAndCity(null, "");
        Assertions.assertEquals("", zipAndCity);
    }

}
