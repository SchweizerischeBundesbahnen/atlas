package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerWithAmazonS3ApiTest;
import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationVersionExportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessOrganisationControllerIntegrationTest extends BaseControllerWithAmazonS3ApiTest {

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

  @Autowired
  private BusinessOrganisationVersionRepository versionRepository;

  @Autowired
  private BusinessOrganisationController controller;

  @Autowired
  private BusinessOrganisationVersionExportService exportService;

  @Autowired
  private AmazonService amazonService;

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
    mvc.perform(post("/v1/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
  }

  @Test
  public void shouldGetBusinessOrganisationVersionsBySboid() throws Exception {
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
    BusinessOrganisationVersionModel businessOrganisationVersion = controller.createBusinessOrganisationVersion(model);

    model.setDescriptionDe("desc-de1-changed");
    model.setValidFrom(LocalDate.of(2002, 1, 1));
    model.setValidTo(LocalDate.of(2002, 12, 31));
    controller.updateBusinessOrganisationVersion(businessOrganisationVersion.getId(), model);

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
  public void shouldNotGetBusinessOrganisationVersionsWhenProvidedSboidDoesNotExists()
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
  public void shouldGetAllBusinessOrganisationVersions() throws Exception {
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
    controller.createBusinessOrganisationVersion(model);

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
  public void shouldNotCreateBusinessOrganisationVersionWhenRequiredAbbreviationDeFieldProvidedIsTooLong()
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
    mvc.perform(post("/v1/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(
            jsonPath("$.details[0].message", is("Value frufrufrufrufrufrufr rejected due to size must be between 1 and 10")))
        .andExpect(jsonPath("$.details[0].field", is("abbreviationFr")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("frufrufrufrufrufrufr")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("size must be between 1 and 10")));
  }

  @Test
  public void shouldNotUpdateBusinessOrganisationVersionWhenRequiredAbbreviationDeFieldNotProvided()
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
    mvc.perform(post("/v1/business-organisations/versions").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.error", is("Method argument not valid error")))
        .andExpect(jsonPath("$.details[0].message", is("Value null rejected due to must not be null")))
        .andExpect(jsonPath("$.details[0].field", is("abbreviationDe")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("rejectedValue")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("null")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("cause")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("must not be null")));
  }

  @Test
  public void shouldNotUpdateBusinessOrganisationVersionWhenProvidedIdDoesNotExists()
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
    mvc.perform(post("/v1/business-organisations/versions/123456789").contentType(contentType)
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
  public void shouldDeleteBusinessOrganisationBySboid() throws Exception {
    //when and then
    mvc.perform(delete("/v1/business-organisations/" + version.getSboid()))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldRevokeBusinessOrganisationBySboid() throws Exception {
    //when and then
    mvc.perform(post("/v1/business-organisations/" + version.getSboid() + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[0]." + BusinessOrganisationVersionModel.Fields.status, is("REVOKED")));
  }

  @Test
  public void shouldReturnConflictErrorResponse() throws Exception {
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
    BusinessOrganisationVersionModel savedVersion = controller.createBusinessOrganisationVersion(model);

    //when and then
    mvc.perform(post("/v1/business-organisations/versions").contentType(contentType)
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
  void shouldReturnOptimisticLockingOnBusinessObjectChanges() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
                                                                            .validFrom(LocalDate.of(2001, 1, 1))
                                                                            .validTo(LocalDate.of(2001, 12, 31))
                                                                            .build();
    versionModel = controller.createBusinessOrganisationVersion(versionModel);

    // When first update it is ok
    versionModel.setValidFrom(LocalDate.of(2010, 1, 1));
    versionModel.setValidTo(LocalDate.of(2010, 12, 31));
    mvc.perform(post(
            "/v1/business-organisations/versions/" + versionModel.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(versionModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    versionModel.setValidFrom(LocalDate.of(2001, 1, 1));
    versionModel.setValidTo(LocalDate.of(2010, 12, 31));
    mvc.perform(post(
            "/v1/business-organisations/versions/" + versionModel.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(versionModel)))
        .andExpect(status().isPreconditionFailed());
  }

  @Test
  void shouldExportFullBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
                                                                            .validFrom(LocalDate.of(2001, 1, 1))
                                                                            .validTo(LocalDate.of(2001, 12, 31))
                                                                            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/full"))
        .andExpect(status().isOk()).andReturn();
    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldReadJsonAfterExportFullBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.of(2001, 1, 1))
            .validTo(LocalDate.of(2001, 12, 31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/full"))
            .andExpect(status().isOk()).andReturn();

    mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.FULL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]."
                    + businessTypes, containsInAnyOrder(BusinessType.RAILROAD.name(), BusinessType.AIR.name(), BusinessType.SHIP.name())))
            .andExpect(jsonPath("$.[0]." + organisationNumber, is(123)))
            .andExpect(jsonPath("$.[0]." + contactEnterpriseEmail, is("mail@mail.ch")))
            .andExpect(jsonPath("$.[0]." + descriptionDe, is("desc-de")))
            .andExpect(jsonPath("$.[0]." + descriptionFr, is("desc-fr")))
            .andExpect(jsonPath("$.[0]." + descriptionIt, is("desc-it")))
            .andExpect(jsonPath("$.[0]." + descriptionEn, is("desc-en")))
            .andExpect(jsonPath("$.[0]." + abbreviationDe, is("de")))
            .andExpect(jsonPath("$.[0]." + abbreviationFr, is("fr")))
            .andExpect(jsonPath("$.[0]." + abbreviationIt, is("it")))
            .andExpect(jsonPath("$.[0]." + abbreviationEn, is("en")));
    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldGetJonGzAfterExportFullBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.of(2001, 1, 1))
            .validTo(LocalDate.of(2001, 12, 31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/full"))
            .andExpect(status().isOk()).andReturn();

    MvcResult mvcResult1 = mvc.perform(get("/v1/business-organisations/export/download-gz-json/" + ExportType.FULL))
            .andExpect(status().isOk()).andReturn();
    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldExportActualBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
                                                                            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
                                                                            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
                                                                            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/actual"))
        .andExpect(status().isOk()).andReturn();
    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldReadJsonAfterExportActualBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/actual"))
            .andExpect(status().isOk()).andReturn();

    mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.ACTUAL_DATE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]."
                    + businessTypes, containsInAnyOrder(BusinessType.RAILROAD.name(), BusinessType.AIR.name(), BusinessType.SHIP.name())))
            .andExpect(jsonPath("$.[0]." + organisationNumber, is(123)))
            .andExpect(jsonPath("$.[0]." + contactEnterpriseEmail, is("mail@mail.ch")))
            .andExpect(jsonPath("$.[0]." + descriptionDe, is("desc-de")))
            .andExpect(jsonPath("$.[0]." + descriptionFr, is("desc-fr")))
            .andExpect(jsonPath("$.[0]." + descriptionIt, is("desc-it")))
            .andExpect(jsonPath("$.[0]." + descriptionEn, is("desc-en")))
            .andExpect(jsonPath("$.[0]." + abbreviationDe, is("de")))
            .andExpect(jsonPath("$.[0]." + abbreviationFr, is("fr")))
            .andExpect(jsonPath("$.[0]." + abbreviationIt, is("it")))
            .andExpect(jsonPath("$.[0]." + abbreviationEn, is("en")));

    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldGetJsonGzAfterExportActualBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/actual"))
            .andExpect(status().isOk()).andReturn();

    MvcResult mvcResult1 = mvc.perform(get("/v1/business-organisations/export/download-gz-json/" + ExportType.ACTUAL_DATE))
            .andExpect(status().isOk()).andReturn();

    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldExportFutureTimetableBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
                                                                            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
                                                                            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
                                                                            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldReadJsonAfterExportTimetableYearChangeBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/timetable-year-change"))
            .andExpect(status().isOk()).andReturn();

    mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.FUTURE_TIMETABLE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]."
                    + businessTypes, containsInAnyOrder(BusinessType.RAILROAD.name(), BusinessType.AIR.name(), BusinessType.SHIP.name())))
            .andExpect(jsonPath("$.[0]." + organisationNumber, is(123)))
            .andExpect(jsonPath("$.[0]." + contactEnterpriseEmail, is("mail@mail.ch")))
            .andExpect(jsonPath("$.[0]." + descriptionDe, is("desc-de")))
            .andExpect(jsonPath("$.[0]." + descriptionFr, is("desc-fr")))
            .andExpect(jsonPath("$.[0]." + descriptionIt, is("desc-it")))
            .andExpect(jsonPath("$.[0]." + descriptionEn, is("desc-en")))
            .andExpect(jsonPath("$.[0]." + abbreviationDe, is("de")))
            .andExpect(jsonPath("$.[0]." + abbreviationFr, is("fr")))
            .andExpect(jsonPath("$.[0]." + abbreviationIt, is("it")))
            .andExpect(jsonPath("$.[0]." + abbreviationEn, is("en")));

    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldGetJsonGzAfterExportTimetableYearChangeBusinessOrganisationVersions() throws Exception {
    //given
    BusinessOrganisationVersionModel versionModel = BusinessOrganisationData.businessOrganisationVersionModelBuilder()
            .validFrom(LocalDate.now().withMonth(1).withDayOfMonth(1))
            .validTo(LocalDate.now().withMonth(12).withDayOfMonth(31))
            .build();
    controller.createBusinessOrganisationVersion(versionModel);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/business-organisations/export/timetable-year-change"))
            .andExpect(status().isOk()).andReturn();

    MvcResult mvcResult1 = mvc.perform(get("/v1/business-organisations/export/download-gz-json/" + ExportType.FUTURE_TIMETABLE))
            .andExpect(status().isOk()).andReturn();

    deleteFileFromBucket(mvcResult, exportService.getDirectory(), amazonService);
  }

  @Test
  void shouldFailToReadJson() throws Exception {
    mvc.perform(get("/v1/business-organisations/export/download-json/" + ExportType.FUTURE_TIMETABLE))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
  }

  @Test
  void shouldFailToGetJsonGz() throws Exception {
    mvc.perform(get("/v1/business-organisations/export/download-gz-json/" + ExportType.FUTURE_TIMETABLE))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
  }

  @Test
  public void shouldReturnErrorMessageOnEmptyBody() throws Exception {
    mvc.perform(post("/v1/business-organisations/versions").contentType(contentType)
                    .content(mapper.writeValueAsString("{}")))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldGetBusinessOrganisationVersions() throws Exception {
    //when and then
    mvc.perform(get("/v1/business-organisations/versions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects[0].sboid").value(version.getSboid()));
  }
}
