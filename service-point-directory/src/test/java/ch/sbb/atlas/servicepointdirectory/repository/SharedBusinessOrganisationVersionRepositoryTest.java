package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class SharedBusinessOrganisationVersionRepositoryTest {

  private final SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  @Autowired
   SharedBusinessOrganisationVersionRepositoryTest(
      SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository) {
    this.sharedBusinessOrganisationVersionRepository = sharedBusinessOrganisationVersionRepository;
  }

  @Test
  void shouldSaveToSharedDbSchema() {
    sharedBusinessOrganisationVersionRepository.save(SharedBusinessOrganisationVersionModel.builder()
        .id(1L)
        .sboid("ch:1:sboid:1234")
        .abbreviationDe("DE")
        .abbreviationFr("FR")
        .abbreviationIt("IT")
        .abbreviationEn("EN")
        .descriptionDe("dütsch")
        .descriptionFr("francais")
        .descriptionIt("italiano pepe hands")
        .descriptionEn("englando")
        .organisationNumber(2)
        .status("VALIDATED")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .build());
    assertThat(sharedBusinessOrganisationVersionRepository.findAll()).hasSize(1);
  }
}