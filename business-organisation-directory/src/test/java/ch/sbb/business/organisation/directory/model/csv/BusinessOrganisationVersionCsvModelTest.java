package ch.sbb.business.organisation.directory.model.csv;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BusinessOrganisationVersionCsvModelTest {

  private final ObjectWriter objectWriter = new AtlasCsvMapper(BusinessOrganisationVersionCsvModel.class).getObjectWriter();

  @Test
  void shouldExportToCorrectTimestampFormat() throws JsonProcessingException {
    BusinessOrganisationVersionCsvModel csvModel = BusinessOrganisationVersionCsvModel.toCsvModel(
        BusinessOrganisationData.businessOrganisationVersionBuilder()
            .creationDate(LocalDateTime.of(2020, 1, 31, 11, 12, 15, 147))
            .build());

    String csvString = objectWriter.writeValueAsString(csvModel);
    String[] csvSplit = csvString.split(";");
    String creationTimeAsString = csvSplit[csvSplit.length - 2].replaceAll("\"", "");

    assertThat(creationTimeAsString).isEqualTo("2020-01-31 11:12:15");
  }
}