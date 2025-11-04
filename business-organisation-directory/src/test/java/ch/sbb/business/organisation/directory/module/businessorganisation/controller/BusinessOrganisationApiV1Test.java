package ch.sbb.business.organisation.directory.module.businessorganisation.controller;

import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.abbreviationDe;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.abbreviationEn;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.abbreviationFr;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.abbreviationIt;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.businessTypes;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.contactEnterpriseEmail;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.descriptionDe;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.descriptionEn;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.descriptionFr;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.descriptionIt;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.organisationNumber;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.validFrom;
import static ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel.Fields.validTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.module.businessorganisation.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class BusinessOrganisationApiV1Test extends BaseControllerApiTest {

  private final BusinessOrganisationVersion version = BusinessOrganisationVersion
      .builder()
      .sboid("ch:1:sboid:1000000")
      .abbreviationDe("de")
      .abbreviationFr("fr")
      .abbreviationIt("it")
      .abbreviationEn("en")
      .descriptionDe("desc-de")
      .descriptionFr("desc-fr")
      .descriptionIt("desc-it")
      .descriptionEn("desc-en")
      .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
      .contactEnterpriseEmail("mail@mail.ch")
      .organisationNumber(123)
      .status(Status.VALIDATED)
      .validFrom(LocalDate.of(2000, 1, 1))
      .validTo(LocalDate.of(2000, 12, 31))
      .build();

  private final BusinessOrganisationVersionRepository versionRepository;

  @MockitoBean
  private AmazonService amazonService;

  @Autowired
  BusinessOrganisationApiV1Test(BusinessOrganisationVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    versionRepository.save(version);
  }

  @AfterEach
  void cleanUpDb() {
    versionRepository.deleteAll();
  }

  @Test
  void shouldGetBusinessOrganisationVersionsBySboid() throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:1000001")
        .abbreviationDe("de1")
        .abbreviationFr("fr1")
        .abbreviationIt("it1")
        .abbreviationEn("en1")
        .descriptionDe("desc-de1")
        .descriptionFr("desc-fr1")
        .descriptionIt("desc-it1")
        .descriptionEn("desc-en1")
        .contactEnterpriseEmail("mail1@mail.ch")
        .organisationNumber(1234)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    BusinessOrganisationVersionModel businessOrganisationVersion =
        mapper.readValue(
            mvc.perform(post("/internal/business-organisations/versions")
                .contentType(contentType)
                .content(mapper.writeValueAsString(model))
            ).andReturn().getResponse().getContentAsByteArray(),
            BusinessOrganisationVersionModel.class
        );

    model.setDescriptionDe("desc-de1-changed");
    model.setValidFrom(LocalDate.of(2002, 1, 1));
    model.setValidTo(LocalDate.of(2002, 12, 31));
    model.setEtagVersion(businessOrganisationVersion.getEtagVersion());

    mvc.perform(put("/internal/business-organisations/versions/" + businessOrganisationVersion.getId())
        .contentType(contentType)
        .content(mapper.writeValueAsString(model))
    );

    //when and then
    mvc.perform(get(
            "/v1/business-organisations/versions/" + businessOrganisationVersion.getSboid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[0]." + validTo, is("2001-12-31")))
        .andExpect(jsonPath("$[0]." + organisationNumber, is(1234)))
        .andExpect(jsonPath("$[0]." + contactEnterpriseEmail, is("mail1@mail.ch")))
        .andExpect(jsonPath("$[0]." + descriptionDe, is("desc-de1")))
        .andExpect(jsonPath("$[0]." + descriptionFr, is("desc-fr1")))
        .andExpect(jsonPath("$[0]." + descriptionIt, is("desc-it1")))
        .andExpect(jsonPath("$[0]." + descriptionEn, is("desc-en1")))
        .andExpect(jsonPath("$[0]." + abbreviationDe, is("de1")))
        .andExpect(jsonPath("$[0]." + abbreviationFr, is("fr1")))
        .andExpect(jsonPath("$[0]." + abbreviationIt, is("it1")))
        .andExpect(jsonPath("$[0]." + abbreviationEn, is("en1")))

        .andExpect(jsonPath("$[1]." + validFrom, is("2002-01-01")))
        .andExpect(jsonPath("$[1]." + validTo, is("2002-12-31")))
        .andExpect(jsonPath("$[1]." + organisationNumber, is(1234)))
        .andExpect(jsonPath("$[1]." + contactEnterpriseEmail, is("mail1@mail.ch")))
        .andExpect(jsonPath("$[1]." + descriptionDe, is("desc-de1-changed")))
        .andExpect(jsonPath("$[1]." + descriptionFr, is("desc-fr1")))
        .andExpect(jsonPath("$[1]." + descriptionIt, is("desc-it1")))
        .andExpect(jsonPath("$[1]." + descriptionEn, is("desc-en1")))
        .andExpect(jsonPath("$[1]." + abbreviationDe, is("de1")))
        .andExpect(jsonPath("$[1]." + abbreviationFr, is("fr1")))
        .andExpect(jsonPath("$[1]." + abbreviationIt, is("it1")))
        .andExpect(jsonPath("$[1]." + abbreviationEn, is("en1")));
  }

  @Test
  void shouldNotGetBusinessOrganisationVersionsWhenProvidedSboidDoesNotExists()
      throws Exception {
    //when and then
    mvc.perform(get("/v1/business-organisations/versions/ch:1:sboid:110000112"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with sboid ch:1:sboid:110000112 not found")))
        .andExpect(jsonPath("$.details[0].field", is("sboid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("sboid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("ch:1:sboid:110000112")));
  }

  @Test
  void shouldGetAllBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:1000001")
        .abbreviationDe("de1")
        .abbreviationFr("fr1")
        .abbreviationIt("it1")
        .abbreviationEn("en1")
        .descriptionDe("desc-de1")
        .descriptionFr("desc-fr1")
        .descriptionIt("desc-it1")
        .descriptionEn("desc-en1")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail1@mail.ch")
        .organisationNumber(1234)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    mvc.perform(post("/internal/business-organisations/versions")
        .contentType(contentType)
        .content(mapper.writeValueAsString(model))
    );

    //when and then
    mvc.perform(get("/v1/business-organisations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.objects[0]."
            + businessTypes, containsInAnyOrder(BusinessType.RAILROAD.name(), BusinessType.AIR.name(), BusinessType.SHIP.name())))
        .andExpect(jsonPath("$.objects[0]." + validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$.objects[0]." + validTo, is("2000-12-31")))
        .andExpect(jsonPath("$.objects[0]." + organisationNumber, is(123)))
        .andExpect(jsonPath("$.objects[0]." + contactEnterpriseEmail, is("mail@mail.ch")))
        .andExpect(jsonPath("$.objects[0]." + descriptionDe, is("desc-de")))
        .andExpect(jsonPath("$.objects[0]." + descriptionFr, is("desc-fr")))
        .andExpect(jsonPath("$.objects[0]." + descriptionIt, is("desc-it")))
        .andExpect(jsonPath("$.objects[0]." + descriptionEn, is("desc-en")))
        .andExpect(jsonPath("$.objects[0]." + abbreviationDe, is("de")))
        .andExpect(jsonPath("$.objects[0]." + abbreviationFr, is("fr")))
        .andExpect(jsonPath("$.objects[0]." + abbreviationIt, is("it")))
        .andExpect(jsonPath("$.objects[0]." + abbreviationEn, is("en")))

        .andExpect(jsonPath("$.objects[1]."
            + businessTypes, containsInAnyOrder(BusinessType.RAILROAD.name(), BusinessType.AIR.name(), BusinessType.SHIP.name())))
        .andExpect(jsonPath("$.objects[1]." + validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$.objects[1]." + validTo, is("2001-12-31")))
        .andExpect(jsonPath("$.objects[1]." + organisationNumber, is(1234)))
        .andExpect(jsonPath("$.objects[1]." + contactEnterpriseEmail, is("mail1@mail.ch")))
        .andExpect(jsonPath("$.objects[1]." + descriptionDe, is("desc-de1")))
        .andExpect(jsonPath("$.objects[1]." + descriptionFr, is("desc-fr1")))
        .andExpect(jsonPath("$.objects[1]." + descriptionIt, is("desc-it1")))
        .andExpect(jsonPath("$.objects[1]." + descriptionEn, is("desc-en1")))
        .andExpect(jsonPath("$.objects[1]." + abbreviationDe, is("de1")))
        .andExpect(jsonPath("$.objects[1]." + abbreviationFr, is("fr1")))
        .andExpect(jsonPath("$.objects[1]." + abbreviationIt, is("it1")))
        .andExpect(jsonPath("$.objects[1]." + abbreviationEn, is("en1")));
  }

  @Test
  void shouldGetBusinessOrganisationVersions() throws Exception {
    //when and then
    mvc.perform(get("/v1/business-organisations/versions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects[0].sboid").value(version.getSboid()));
  }

  @Test
  void shouldReturnBadRequestWhenPageSizeExceeded() throws Exception {
    mvc.perform(get("/v1/business-organisations?size=5000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("The page size is limited to 2000")));
  }

  @Test
  void shouldGetBusinessOrganisationsByValidToFromDate() throws Exception {
    BusinessOrganisationVersion businessOrganisation = BusinessOrganisationVersion
        .builder()
        .sboid("ch:1:sboid:1000008")
        .abbreviationDe("de")
        .abbreviationFr("fr")
        .abbreviationIt("it")
        .abbreviationEn("en")
        .descriptionDe("desc-de")
        .descriptionFr("desc-fr")
        .descriptionIt("desc-it")
        .descriptionEn("desc-en")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail8@mail.ch")
        .organisationNumber(1238)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2015, 1, 1))
        .validTo(LocalDate.of(2015, 12, 31))
        .build();
    versionRepository.save(businessOrganisation);
    mvc.perform(get("/v1/business-organisations/versions?validToFromDate=" + businessOrganisation.getValidFrom()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

}
