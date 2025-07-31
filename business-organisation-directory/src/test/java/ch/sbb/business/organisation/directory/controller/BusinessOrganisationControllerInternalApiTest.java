package ch.sbb.business.organisation.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BusinessOrganisationControllerInternalApiTest extends BaseControllerApiTest {

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

  @Autowired
  BusinessOrganisationControllerInternalApiTest(BusinessOrganisationVersionRepository versionRepository) {
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
  void shouldCreateBusinessOrganisationVersion() throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:100000")
        .abbreviationDe("abkde")
        .abbreviationFr("abkfr")
        .abbreviationIt("abkit")
        .abbreviationEn("abken")
        .descriptionDe("desc-de")
        .descriptionFr("desc-fr")
        .descriptionIt("desc-it")
        .descriptionEn("desc-en")
        .businessTypes(new HashSet<>(Arrays.asList(BusinessType.RAILROAD, BusinessType.AIR, BusinessType.SHIP)))
        .contactEnterpriseEmail("mail@mail.ch")
        .organisationNumber(1234)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    //when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldNotCreateBusinessOrganisationVersionWhenRequiredAbbreviationDeFieldProvidedIsTooLong()
      throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:100000")
        .abbreviationDe("de")
        .abbreviationFr("frufrufrufrufrufrufr")
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

    //when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message",
            is("Following constraints were violated: [Property 'abbreviationFr' has invalid value: 'frufrufrufrufrufrufr']")))
        .andExpect(jsonPath("$.error", is("Constraint violation")))
        .andExpect(
            jsonPath("$.details[0].message", is("size must be between 1 and 10")))
        .andExpect(jsonPath("$.details[0].field", is("abbreviationFr")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.SIZE")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("propertyPath")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("abbreviationFr")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("frufrufrufrufrufrufr")));
  }

  @Test
  void shouldNotUpdateBusinessOrganisationVersionWhenRequiredAbbreviationDeFieldNotProvided()
      throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:100000")
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

    //when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message",
            is("Following constraints were violated: [Property 'abbreviationDe' has invalid value: 'null']")))
        .andExpect(jsonPath("$.error", is("Constraint violation")))
        .andExpect(jsonPath("$.details[0].message", is("must not be null")))
        .andExpect(jsonPath("$.details[0].field", is("abbreviationDe")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.NOT_NULL")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("propertyPath")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("abbreviationDe")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("null")));
  }

  @Test
  void shouldNotUpdateBusinessOrganisationVersionWhenProvidedIdDoesNotExists()
      throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
        .sboid("ch:1:sboid:100000")
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

    //when and then
    mvc.perform(put("/internal/business-organisations/versions/123456789").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with id 123456789 not found")))
        .andExpect(jsonPath("$.details[0].field", is("id")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("id")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123456789")));
  }

  @Test
  void shouldDeleteBusinessOrganisationBySboid() throws Exception {
    //when and then
    mvc.perform(delete("/internal/business-organisations/" + version.getSboid()))
        .andExpect(status().isOk());
  }

  @Test
  void shouldRevokeBusinessOrganisationBySboid() throws Exception {
    //when and then
    mvc.perform(post("/internal/business-organisations/" + version.getSboid() + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0]." + BusinessOrganisationVersionModel.Fields.status, is("REVOKED")));
  }

  @Test
  void shouldReturnConflictErrorResponse() throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
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
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    BusinessOrganisationVersionModel savedVersion =
        mapper.readValue(
            mvc.perform(post("/internal/business-organisations/versions")
                .contentType(contentType)
                .content(mapper.writeValueAsString(model))
            ).andReturn().getResponse().getContentAsByteArray(),
            BusinessOrganisationVersionModel.class
        );

    //when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)))
        .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule")))
        .andExpect(jsonPath("$.error", is("BO conflict")))
        .andExpect(jsonPath("$.details", hasSize(5)))
        .andExpect(jsonPath("$.details[0].message", is(
            "abbreviationDe de1 already taken from 01.01.2001 to 31.12.2001 by "
                + savedVersion.getSboid())))
        .andExpect(jsonPath("$.details[1].message", is(
            "abbreviationEn en1 already taken from 01.01.2001 to 31.12.2001 by "
                + savedVersion.getSboid())))
        .andExpect(jsonPath("$.details[2].message", is(
            "abbreviationFr fr1 already taken from 01.01.2001 to 31.12.2001 by "
                + savedVersion.getSboid())))
        .andExpect(jsonPath("$.details[3].message", is(
            "abbreviationIt it1 already taken from 01.01.2001 to 31.12.2001 by "
                + savedVersion.getSboid())))
        .andExpect(jsonPath("$.details[4].message", is(
            "organisationNumber 1234 already taken from 01.01.2001 to 31.12.2001 by "
                + savedVersion.getSboid())));
  }

  @Test
  void shouldNotReturnConflictErrorResponse() throws Exception {
    //given
    BusinessOrganisationVersionModel model = BusinessOrganisationVersionModel
        .builder()
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
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    BusinessOrganisationVersionModel savedVersion =
        mapper.readValue(
            mvc.perform(post("/internal/business-organisations/versions")
                .contentType(contentType)
                .content(mapper.writeValueAsString(model))
            ).andReturn().getResponse().getContentAsByteArray(),
            BusinessOrganisationVersionModel.class
        );

    mvc.perform(post("/internal/business-organisations/" + savedVersion.getSboid() + "/revoke"));

    //when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnOptimisticLockingOnBusinessObjectChanges() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();
    versionModel =
        mapper.readValue(
            mvc.perform(post("/internal/business-organisations/versions")
                .contentType(contentType)
                .content(mapper.writeValueAsString(versionModel))
            ).andReturn().getResponse().getContentAsByteArray(),
            BusinessOrganisationVersionModel.class
        );

    // When first update it is ok
    versionModel.setValidFrom(LocalDate.of(2010, 1, 1));
    versionModel.setValidTo(LocalDate.of(2010, 12, 31));
    mvc.perform(put(
            "/internal/business-organisations/versions/" + versionModel.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(versionModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    versionModel.setValidFrom(LocalDate.of(2001, 1, 1));
    versionModel.setValidTo(LocalDate.of(2010, 12, 31));
    mvc.perform(put(
            "/internal/business-organisations/versions/" + versionModel.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(versionModel)))
        .andExpect(status().isPreconditionFailed());
  }

  @Test
  void shouldReturnErrorMessageOnEmptyBody() throws Exception {
    // when and then
    mvc.perform(post("/internal/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString("{}")))
        .andExpect(status().isBadRequest());
  }

}
