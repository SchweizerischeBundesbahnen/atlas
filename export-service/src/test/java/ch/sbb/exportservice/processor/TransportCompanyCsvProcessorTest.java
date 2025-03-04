package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.exportservice.entity.bodi.TransportCompany;
import ch.sbb.exportservice.model.TransportCompanyCsvModel;
import static ch.sbb.exportservice.processor.BaseServicePointProcessor.LOCAL_DATE_FORMATTER;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TransportCompanyCsvProcessorTest {

  private final TransportCompanyCsvProcessor processor = new TransportCompanyCsvProcessor();

  @Test
  void shouldMapToCsvModel() {
    LocalDateTime creationDate = LocalDateTime.now();
    LocalDateTime editionDate = LocalDateTime.now();
    TransportCompany entity = TransportCompany.builder()
        .id(1L)
        .number("12345")
        .abbreviation("abbreviation")
        .description("description")
        .businessRegisterName("businessRegisterName")
        .transportCompanyStatus(TransportCompanyStatus.CURRENT)
        .businessRegisterNumber("businessRegisterNumber")
        .enterpriseId("enterpriseId")
        .ricsCode("ricsCode")
        .businessOrganisationNumbers("businessOrganisationNumbers")
        .comment("comment")
        .creationDate(creationDate)
        .editionDate(editionDate)
        .build();

    TransportCompanyCsvModel expected = TransportCompanyCsvModel.builder()
        .id(1L)
        .number("12345")
        .abbreviation("abbreviation")
        .description("description")
        .businessRegisterName("businessRegisterName")
        .transportCompanyStatus("CURRENT")
        .businessRegisterNumber("businessRegisterNumber")
        .enterpriseId("enterpriseId")
        .ricsCode("ricsCode")
        .businessOrganisationNumbers("businessOrganisationNumbers")
        .comment("comment")
        .creationDate(LOCAL_DATE_FORMATTER.format(creationDate))
        .editionDate(LOCAL_DATE_FORMATTER.format(editionDate))
        .build();

    TransportCompanyCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }
}