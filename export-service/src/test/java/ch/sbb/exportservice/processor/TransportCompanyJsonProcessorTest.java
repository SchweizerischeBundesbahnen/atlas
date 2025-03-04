package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.exportservice.entity.bodi.TransportCompany;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TransportCompanyJsonProcessorTest {

  @Test
  void shouldMapToReadModel() {
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
        .creationDate(LocalDateTime.now())
        .editionDate(LocalDateTime.now())
        .build();

    TransportCompanyJsonProcessor processor = new TransportCompanyJsonProcessor();

    TransportCompanyModel expected = TransportCompanyModel.builder()
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
        .build();

    TransportCompanyModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }
}
