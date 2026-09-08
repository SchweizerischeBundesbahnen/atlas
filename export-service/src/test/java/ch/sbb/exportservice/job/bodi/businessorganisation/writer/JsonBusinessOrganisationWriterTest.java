package ch.sbb.exportservice.job.bodi.businessorganisation.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectMarshaller;

class JsonBusinessOrganisationWriterTest {

  @Test
  void shouldWriteDatesAndTimestampsCorrectly() {
    JsonBusinessOrganisationWriter jsonBusinessOrganisationWriter = new JsonBusinessOrganisationWriter(mock(FileService.class));
    JacksonJsonObjectMarshaller<BusinessOrganisationVersionModel> jsonMarshaller =
        jsonBusinessOrganisationWriter.createJsonMarshaller();

    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel.builder()
        .creationDate(LocalDateTime.of(2024, 6, 20, 14, 30, 0))
        .editionDate(LocalDateTime.of(2024, 8, 20, 14, 30, 0))
        .validFrom(LocalDate.of(2024, 7, 1))
        .validTo(LocalDate.of(2025, 6, 30))
        .abbreviationDe("de")
        .build();

    String expected = """
        {"abbreviationDe":"de","abbreviationEn":null,"abbreviationFr":null,"abbreviationIt":null,"businessTypes":null,"contactEnterpriseEmail":null,"creationDate":"2024-06-20T14:30:00","creator":null,"descriptionDe":null,"descriptionEn":null,"descriptionFr":null,"descriptionIt":null,"editionDate":"2024-08-20T14:30:00","editor":null,"etagVersion":null,"id":null,"organisationNumber":null,"said":null,"sboid":null,"status":null,"validFrom":"2024-07-01","validTo":"2025-06-30"}""";

    String jsonString = jsonMarshaller.marshal(model);
    assertThat(jsonString).isEqualTo(expected);
  }

}