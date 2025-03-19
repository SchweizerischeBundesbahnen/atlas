package ch.sbb.exportservice.job.transportcompany;

import static ch.sbb.exportservice.utile.MapperUtil.LOCAL_DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.exportservice.job.bodi.transportcompany.TransportCompany;
import ch.sbb.exportservice.job.bodi.transportcompany.TransportCompanyCsvModel;
import ch.sbb.exportservice.job.bodi.transportcompany.TransportCompanyCsvProcessor;
import java.time.LocalDateTime;
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