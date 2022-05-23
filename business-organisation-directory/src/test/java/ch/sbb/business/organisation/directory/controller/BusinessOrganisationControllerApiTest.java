package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessOrganisationControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private BusinessOrganisationVersionRepository versionRepository;

  private final BusinessOrganisationVersion version =
      BusinessOrganisationVersion.builder()
                                 .sboid("ch:1:sboid:1000000")
                                 .abbreviationDe("de")
                                 .abbreviationFr("fr")
                                 .abbreviationIt("it")
                                 .abbreviationEn("en")
                                 .descriptionDe("desc-de")
                                 .descriptionFr("desc-fr")
                                 .descriptionIt("desc-it")
                                 .descriptionEn("desc-en")
                                 .businessTypes(new HashSet<>(
                                     Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                         BusinessType.SHIP)))
                                 .contactEnterpriseEmail("mail@mail.ch")
                                 .organisationNumber(123)
                                 .status(Status.ACTIVE)
                                 .validFrom(LocalDate.of(2000, 1, 1))
                                 .validTo(LocalDate.of(2000, 12, 31))
                                 .build();

  @BeforeEach
  void createDefaultVersion() {
    versionRepository.save(version);
  }

  @AfterEach
  void cleanUpDb() {
    versionRepository.deleteAll();
  }

  @Test
  public void shouldCreateBusinessOrganisationVersion() throws Exception {
    //given
    BusinessOrganisationVersionModel model =
        BusinessOrganisationVersionModel.builder()
                                        .sboid("ch:1:sboid:100000")
                                        .abbreviationDe("de")
                                        .abbreviationFr("fr")
                                        .abbreviationIt("it")
                                        .abbreviationEn("en")
                                        .descriptionDe("desc-de")
                                        .descriptionFr("desc-fr")
                                        .descriptionIt("desc-it")
                                        .descriptionEn("desc-en")
                                        .businessTypes(new HashSet<>(
                                            Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR,
                                                BusinessType.SHIP)))
                                        .contactEnterpriseEmail("mail@mail.ch")
                                        .organisationNumber(123)
                                        .status(Status.ACTIVE)
                                        .validFrom(LocalDate.of(2000, 1, 1))
                                        .validTo(LocalDate.of(2000, 12, 31))
                                        .build();

    //when and then
    mvc.perform(post("/v1/business-organisations/versions")
        .contentType(contentType)
        .content(mapper.writeValueAsString(model))
    ).andExpect(status().isCreated());

  }


}