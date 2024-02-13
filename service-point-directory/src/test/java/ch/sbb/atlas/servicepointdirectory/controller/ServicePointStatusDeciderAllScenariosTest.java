package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicePointStatusDeciderAllScenariosTest extends BaseControllerApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;
  @MockBean
  private GeoReferenceService geoReferenceService;
  @MockBean
  private LocationService locationService;

  private final ServicePointVersionRepository repository;
  private final ServicePointController servicePointController;

  @Autowired
  ServicePointStatusDeciderAllScenariosTest(ServicePointVersionRepository repository,
      ServicePointController servicePointController) {
    this.repository = repository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
    when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReference);
    when(locationService.generateSloid(SloidType.SERVICE_POINT,Country.SWITZERLAND)).thenReturn("ch:1:sloid:1");
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  /**
   * Szenario 1: Neue Haltestelle erfassen
   * <p>
   * NEU:       |________________Haltestelle_________________|
   * <p>
   * IST:
   * Status:
   * <p>
   * RESULTAT:  |________________Haltestelle_________________|
   * Status:                       DRAFT
   */
  @Test
  void scenario1WhenCreateNewStopPointThenSetStatusToDRAFT() throws Exception {
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 2: Umwandlung in Haltestelle (stopPoint = true)
   * <p>
   * NEU:                                |________________Haltestelle______________________|
   * <p>
   * IST:       |__________________________Dienststelle____________________________________|
   * Status:                                VALIDATED
   * <p>
   * RESULTAT:  |______Dienststelle______|______________Haltestelle________________________|
   * Status:          VALIDATED                             DRAFT
   */
  @Test
  void scenario2WhenServicePointAndUpdateToStopPointThenStopPointDRAFT() throws Exception {
    CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint.setMeansOfTransport(new ArrayList<>());
    servicePoint.setStopPointType(null);
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        servicePoint);
    Long id = servicePointVersionModel.getId();

    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint.setValidFrom(LocalDate.of(2011, 12, 11));
    stopPoint.setValidTo(LocalDate.of(2019, 8, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2011-12-10")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2011-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 3: Umwandlung in Haltestelle (stopPoint = true)
   * <p>
   * NEU:                                                               |________________Haltestelle____________________________|
   * <p>
   * IST:       |__________Dienststelle___________|____________Dienststelle__________________|___________Dienststelle___________|
   * Status:                 VALIDATED                         VALIDATED                                   VALIDATED
   * <p>
   * RESULTAT:  |__________Dienststelle___________|__Dienststelle_______|____Haltestelle_____|___Haltestelle____________________|
   * Status:                VALIDATED                  VALIDATED               DRAFT              DRAFT
   */
  @Test
  void scenario3WhenThreeServicePointsAndUpdateToStopPointThenStopPointDRAFT() throws Exception {
    CreateServicePointVersionModel servicePoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint1.setMeansOfTransport(new ArrayList<>());
    servicePoint1.setStopPointType(null);
    servicePoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    servicePoint1.setValidTo(LocalDate.of(2011, 12, 31));
    servicePoint1.setDesignationLong("ABC1");
    servicePoint1.setCategories(List.of(Category.DISTRIBUTION_POINT));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        servicePoint1);
    UpdateServicePointVersionModel servicePoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint2.setMeansOfTransport(new ArrayList<>());
    servicePoint2.setStopPointType(null);
    servicePoint2.setValidFrom(LocalDate.of(2012, 1, 1));
    servicePoint2.setValidTo(LocalDate.of(2014, 12, 31));
    servicePoint2.setDesignationLong("ABC2");
    servicePoint2.setCategories(List.of(Category.MIGRATION_CENTRAL_SERVICE));
    servicePointController.updateServicePoint(servicePointVersionModel1.getId(),
        servicePoint2);
    UpdateServicePointVersionModel servicePoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint3.setMeansOfTransport(new ArrayList<>());
    servicePoint3.setStopPointType(null);
    servicePoint3.setValidFrom(LocalDate.of(2015, 1, 1));
    servicePoint3.setValidTo(LocalDate.of(2019, 8, 10));
    servicePoint3.setDesignationLong("ABC3");
    servicePoint3.setCategories(List.of(Category.BORDER_POINT));
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(
        servicePointVersionModel1.getId(),
        servicePoint3);
    Long id = servicePointVersionModels.get(1).getId();
    servicePointController.getServicePointVersions(servicePointVersionModels.get(0).getNumber().getNumber());

    UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint.setDesignationLong(
        "ABC2");
    stopPoint.setValidFrom(LocalDate.of(2013, 1, 1));
    stopPoint.setValidTo(LocalDate.of(2019, 8, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2011-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2012-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2012-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2013-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2014-12-31")))
        .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[3]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
        .andExpect(jsonPath("$[3]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[3].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 4: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:                               |_________Haltestelle B Hausen_______________|
   * <p>
   * IST:       |___________________Haltestelle A Hausen_____________________________|
   * Status:                             VALIDATED
   * <p>
   * RESULTAT:  |__Haltestelle A Hausen__|__________Haltestelle B Hausen_____________|
   * Status:          VALIDATED                           DRAFT
   */
  @Test
  void scenario4WhenStopPointAndChangeStopPointNameOnSecondPartThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        servicePoint);
    Long id = servicePointVersionModel.getId();

    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint.setDesignationOfficial("B Hausen");
    stopPoint.setValidFrom(LocalDate.of(2015, 12, 11));
    stopPoint.setValidTo(LocalDate.of(2019, 8, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-10")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 5: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:       |________________Haltestelle B Hausen________|
   * <p>
   * IST:       |____________________Haltestelle A Hausen____________________________|
   * Status:                             VALIDATED
   * <p>
   * RESULTAT:  |__________Haltestelle B Hausen_____________|__Haltestelle A Hausen__|
   * Status:                  DRAFT                               VALIDATED
   */
  @Test
  void scenario5WhenStopPointAndChangeStopPointNameOnFirstPartThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
    servicePoint.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        servicePoint);
    Long id = servicePointVersionModel.getId();

    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint.setDesignationOfficial("B Hausen");
    stopPoint.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint.setValidTo(LocalDate.of(2015, 12, 31));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 6: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:                                                                             |__Haltestelle C Hausen__|
   * <p>
   * IST:       |______________Haltestelle A Hausen__________|_________________Haltestelle B Hausen____________|
   * Status:                    VALIDATED                                        VALIDATED
   * <p>
   * RESULTAT:  |___________Haltestelle A Hausen_____________|__Haltestelle B Hausen__|__Haltestelle C Hausen__|
   * Status:                  VALIDATED                              VALIDATED                DRAFT
   */
  @Test
  void scenario6WhenTwoStopPointsAndChangeStopPointNameOnLastPartThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setDesignationOfficial("A Hausen");
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2017, 1, 1));

    mvc.perform(put("/v1/service-points/" + id1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2016-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2017-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 7: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:                                         |__Haltestelle C Hausen__|
   * <p>
   * IST:       |____________Haltestelle A Hausen____________|______________Haltestelle B Hausen_______________|
   * Status:                    VALIDATED                                     VALIDATED
   * <p>
   * RESULTAT:  |_____Haltestelle A Hausen________|__Haltestelle C Hausen__|_______Haltestelle B Hausen________|
   * Status:                VALIDATED                        DRAFT                    VALIDATED
   */
  @Test
  void scenario7WhenTwoStopPointsAndChangeStopPointNameInTheMiddleThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2015, 1, 1));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2016, 12, 31));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2015, 6, 1));
    stopPoint3.setValidTo(LocalDate.of(2016, 6, 1));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-05-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-06-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2016-06-01")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-06-02")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2016-12-31")))
        .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 8: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:                                |_______Haltestelle C Hausen_______|
   * <p>
   * IST:       |_______________________________Haltestelle A Hausen___________________________________________|
   * Status:                                       VALIDATED
   * <p>
   * RESULTAT:  |__Haltestelle A Hausen__|_______Haltestelle C Hausen_______|________Haltestelle A Hausen______|
   * Status:          VALIDATED                       DRAFT                              VALIDATED
   */
  @Test
  void scenario8WhenStopPointAndChangeStopPointNameInTheMiddleThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2014, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2016, 12, 31));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2013-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2014-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2016-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2017-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 9: Haltestelle (stopPoint = true) Namensänderung
   * <p>
   * NEU:       |__________________________Haltestelle C Hausen________________________________________________|
   * <p>
   * IST:       |__________________________Haltestelle A Hausen________________________________________________|
   * Status:                                 VALIDATED
   * <p>
   * RESULTAT:  |__________________________Haltestelle C Hausen________________________________________________|
   * Status:                                   DRAFT
   */
  @Test
  void scenario9WhenStopPointAndChangeStopPointNameThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 10: Haltestelle (stopPoint = true) Namensänderung + wieteres Attribut (Category)
   * <p>
   * NEU:                                                                                             |________Haltestelle B
   * Hausen + Category 3_______|
   * <p>
   * IST:       |________________Haltestelle A Hausen + Category 1________|________________Haltestelle A Hausen + Category
   * 2___________________________|
   * Status:                         VALIDATED                                                      VALIDATED
   * <p>
   * RESULTAT:  |_____________Haltestelle A Hausen + Category 1___________|_HS A Hausen + Category 2__|________Haltestelle B
   * Hausen + Category 3 ______|
   * Status:                           VALIDATED                                    VALIDATED                           DRAFT
   */
  @Test
  void scenario10WhenTwoStopPointsWith2CategoriesAndChangeStopPointNameAndCategoryAtTheEndThenStopPointWithNewNameDRAFT()
      throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setDesignationOfficial("A Hausen");
    stopPoint1.setCategories(List.of(Category.POINT_OF_SALE));
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    stopPoint2.setDesignationOfficial("A Hausen");
    stopPoint2.setCategories(List.of(Category.BORDER_POINT));
    List<ReadServicePointVersionModel> servicePointVersionModel1 = servicePointController.updateServicePoint(id,
        stopPoint2);
    Long id1 = servicePointVersionModel1.get(1).getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(
        pointVersion -> pointVersion.setServicePointGeolocation(ServicePointTestData.getAargauServicePointGeolocation()));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("B Hausen");
    stopPoint3.setCategories(List.of(Category.MAINTENANCE_POINT));
    stopPoint3.setValidFrom(LocalDate.of(2018, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getZurichServicePointGeolocation()));

    mvc.perform(put("/v1/service-points/" + id1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2017-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2018-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 11: Haltestelle (stopPoint = true) Namensänderung + wieteres Attribut (Koordinatensänderung)
   * <p>
   * NEU:                                              |_______________________________Haltestelle B Hausen + Koordinaten 3
   * _______________________________________________|
   * <p>
   * IST:       |_______________________Haltestelle A Hausen + Koordinaten 1__________________|_____________________Haltestelle A
   * Hausen + Koordinaten 2___________________|
   * Status:                                 VALIDATED
   * VALIDATED
   * <p>
   * RESULTAT:  |_Haltestelle A Hausen + Koordinaten 1_|_Haltestelle B Hausen + Koordinaten 3_|___________________Haltestelle A
   * Hausen + Koordinaten 2_____________________|
   * Status:                  VALIDATED                               DRAFT                                                 DRAFT
   */
  @Test
  void scenario11WhenTwoStopPointsWith2CoordinatesAndChangeStopPointNameAndCoordinateThenStopPointWithNewNameDRAFT()
      throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    stopPoint2.setDesignationOfficial("A Hausen");
    stopPoint2.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));

    List<ReadServicePointVersionModel> servicePointVersionModel1 = servicePointController.updateServicePoint(id,
        stopPoint2);
    Long id1 = servicePointVersionModel1.get(1).getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("B Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2015, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));

    mvc.perform(put("/v1/service-points/" + id1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2014-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 12: Haltestelle (stopPoint = true) Verlängerung mit Namenwechseln
   * <p>
   * NEU:                                                                                                      |__Verlängerung &
   * Wechseln C Hausen__|
   * <p>
   * IST:       |______________Haltestelle A Hausen__________|__________________Haltestelle B Hausen___________|
   * Status:                      VALIDATED                                         VALIDATED
   * <p>
   * RESULTAT:  |______________Haltestelle A Hausen__________|________________Haltestelle B Hausen______________|__Verlängerung &
   * Wechseln C Hausen__|
   * Status:                       VALIDATED                                         VALIDATED
   * DRAFT
   */
  @Test
  void scenario12WhenTwoStopPointsWith2NamesAndStopPointNameChangeAndExtendThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2019, 8, 11));
    stopPoint3.setValidTo(LocalDate.of(2020, 12, 31));

    mvc.perform(put("/v1/service-points/" + id1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2019-08-11")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2020-12-31")))
        .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 13: Haltestelle (stopPoint = true) Verlängerung mit Namenwechseln
   * <p>
   * NEU:       |__Verlängerung & Wechseln C Hausen__|
   * <p>*
   * IST:                                            |_____________Haltestelle A Hausen___________|________________Haltestelle B
   * Hausen_____________|
   * Status:                                                        VALIDATED                                         VALIDATED
   * <p>
   * RESULTAT:  |__Verlängerung & Wechseln C Hausen__|_____________Haltestelle A Hausen___________|_________________Haltestelle B
   * Hausen_____________|
   * Status:                 DRAFT                                  VALIDATED                                          VALIDATED
   */
  @Test
  void scenario13WhenTwoStopPointsWith2NamesAndStopPointNameChangeAndExtendThenStopPointWithNewNameDRAFT() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setDesignationOfficial("A Hausen");
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("C Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2005, 8, 11));
    stopPoint3.setValidTo(LocalDate.of(2010, 12, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2005-08-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2010-12-10")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 14: Haltestelle (stopPoint = true) Verlängerung ohne Namenwechsel
   * <p>
   * NEU:                                                                                                      |__Verlängerung B
   * Hausen__|
   * <p>
   * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
   * Status:                       VALIDATED                                          VALIDATED
   * <p>
   * RESULTAT:  |________________Haltestelle A Hausen________|_____________________Haltestelle B
   * Hausen__________________________________|
   * Status:                       VALIDATED                                          VALIDATED
   */
  @Test
  void scenario14WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointValidated() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("B Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2019, 8, 11));
    stopPoint3.setValidTo(LocalDate.of(2020, 12, 31));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2020-12-31")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 15: Haltestelle (stopPoint = true) Verlängerung ohne Namenwechsel
   * <p>
   * NEU:       |__Verlängerung A Hausen__|
   * <p>
   * IST:                                 |_____________Haltestelle A Hausen___________|________________Haltestelle B
   * Hausen_____________|
   * Status:                                               VALIDATED                                         VALIDATED
   * <p>
   * RESULTAT:  |______________________Haltestelle A Hausen____________________________|_________________Haltestelle B
   * Hausen____________|
   * Status:                              VALIDATED                                                           VALIDATED
   */
  @Test
  void scenario15WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStatusValidated() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("A Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2005, 8, 1));
    stopPoint3.setValidTo(LocalDate.of(2010, 12, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2005-08-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 16: Haltestelle (stopPoint = true) Verlängerung ohne Namenwechsel über andere Version
   * <p>
   * NEU:                                               |_______________________Wechsel zu B Hausen____________|
   * <p>
   * IST:       |________________Haltestelle A Hausen________|________________Haltestelle B Hausen_____________|
   * Status:                        VALIDATED                                    VALIDATED
   * <p>
   * RESULTAT:  |________________Haltestelle A Hausen___|_____________________Haltestelle B Hausen_____________|
   * Status:                         VALIDATED                                   VALIDATED
   */
  @Test
  void scenario16WhenTwoStopPointsWith2NamesAndStopPointUpdateWithoutNameChangeThenStopPointValidated() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("B Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2015, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2014-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 17: Haltestelle (stopPoint = true) Verlängerung ohne Namenwechsel über andere Version
   * <p>
   * NEU:       |_______________________Wechsel zu A Hausen____________|
   * <p>
   * IST:       |________________Haltestelle A Hausen________|_______________Haltestelle B Hausen______________|
   * Status:                        VALIDATED                                    VALIDATED
   * <p>
   * RESULTAT:  |________________Haltestelle A Hausen__________________|___________Haltestelle B Hausen________|
   * Status:                         VALIDATED                                        VALIDATED
   */
  @Test
  void scenario17WhenTwoStopPointsWith2NamesAndStopPointUpdateThenStopPointStatusValidated() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint2.setDesignationOfficial("B Hausen");
    stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
    stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
        stopPoint2);
    Long id1 = servicePointVersionModel1.getId();
    Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
    servicePointVersion2.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion2.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("A Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2010, 12, 11));
    stopPoint3.setValidTo(LocalDate.of(2017, 12, 10));

    mvc.perform(put("/v1/service-points/" + id1)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2017-12-10")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[0].designationOfficial", is("A Hausen")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2017-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1].designationOfficial", is("B Hausen")));
  }

  /**
   * Szenario 18: Haltestelle (stopPoint = true) Wiedereinführung mit Namenwechsel, mit Lücke
   * <p>
   * NEU:                                                                    |________________Wiedereinführung & Wechsel zu B
   * Hausen____________|
   * <p>
   * IST:       |___________Haltestelle A Hausen_____________|
   * Status:                    VALIDATED
   * <p>
   * RESULTAT:  |___________Haltestelle A Hausen_____________|               |________________________Haltestelle B
   * Hausen______________________|
   * Status:                    VALIDATED                                                                  DRAFT
   */
  @Test
  void scenario18WhenStopPointAndNewStopPointWithGapThenNewStopPointDraft() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setDesignationOfficial("B Hausen");
    stopPoint3.setValidFrom(LocalDate.of(2018, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2018-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 19: Haltestelle (stopPoint = true) Wiedereinführung ohne Namenwechsel, mit Lücke
   * <p>
   * NEU:                                                                    |________________Wiedereinführung ohne
   * Wechsel____________|
   * <p>
   * IST:       |_____________Haltestelle A Hausen___________|
   * Status:                      VALIDATED
   * <p>
   * RESULTAT:  |_____________Haltestelle A Hausen____________|              |__________________Haltestelle A
   * Hausen___________________|
   * Status:                      VALIDATED                                                            DRAFT
   */
  @Test
  void scenario19WhenStopPointAndNewStopPointWithGapAndSameNameThenNewStopPointDraft() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setValidFrom(LocalDate.of(2018, 1, 1));
    stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));
    stopPoint3.setDesignationOfficial("A Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2018-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())));
  }

  @Test
  void newStopPointWhenIsSwissCountryCodeFalseShouldSetStatusToValidated() throws Exception {
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel.setCountry(Country.ITALY);
    createServicePointVersionModel.setNumberShort(12345);
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  @Test
  void newServicePointShouldSetStatusToValidated() throws Exception {
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel.setMeansOfTransport(null);
    createServicePointVersionModel.setOperatingPointRouteNetwork(false);
    createServicePointVersionModel.setStopPointType(null);
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  @Test
  void newStopPointWhenValidityIsNotLongEnoughShouldSetStatusToValidated() throws Exception {
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel.setValidFrom(LocalDate.of(2020, 1, 1));
    createServicePointVersionModel.setValidTo(LocalDate.of(2020, 1, 31));
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  @Test
  void newStopPointWhenValidityIsExactly60DaysShouldSetStatusToValidated() throws Exception {
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel.setValidFrom(LocalDate.of(2020, 1, 1));
    createServicePointVersionModel.setValidTo(LocalDate.of(2020, 2, 29));
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  @Test
  void newStopPointWhenValidityIsExactlyEnoughShouldSetStatusToDraft() throws Exception {
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel.setValidFrom(LocalDate.of(2020, 1, 1));
    createServicePointVersionModel.setValidTo(LocalDate.of(2020, 3, 1));
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.DRAFT.toString())));
  }

  @Test
  void newStopPointWhenValidityIsNotSwissLocationShouldSetStatusToValidated() throws Exception {
    GeoReference geoReference = GeoReference.builder().country(Country.ITALY).build();
    when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReference);
    CreateServicePointVersionModel createServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  /**
   * Szenario 20: Haltestelle (stopPoint = true) in Status DRAFT, update mit Geolocation ändern
   * <p>
   * NEU:       |________________Haltestelle A Hausen + Geolocation B____________|
   * <p>
   * IST:       |________________Haltestelle A Hausen + Geolocation A____________|
   * Status:                      DRAFT
   * <p>
   * RESULTAT:  |________________Haltestelle A Hausen + Geolocation B____________|
   * Status:                      DRAFT
   */
  @Test
  void scenario20WhenSwissStopPointAndUpdateStopPointWithNewSwissGeolocationThenStopPointDraft() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint3.setDesignationOfficial("A Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
  }

  /**
   * Szenario 21: Haltestelle (stopPoint = true) in Status VALIDATED und Geolocation im Ausland, update mit Geolocation ändern in
   * der Schweiz
   * <p>
   * NEU:       |________________Haltestelle A Hausen + Geolocation Switzerland____________|
   * <p>
   * IST:       |________________Haltestelle A Hausen + Geolocation France_________________|
   * Status:                      VALIDATED
   * <p>
   * RESULTAT:  |________________Haltestelle A Hausen + Geolocation Switzerland____________|
   * Status:                      DRAFT
   */
  @Test
  void scenario21WhenStopPointWithFrenchGeolocationAndUpdateStopPointWithNewSwissGeolocationThenStopPointDraft()
      throws Exception {
    GeoReference geoReferenceFrance = GeoReference.builder().country(Country.FRANCE).build();
    when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceFrance);
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    // Check that status for french geolocation is validated
    assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

    GeoReference geoReferenceSwitzerland = GeoReference.builder().country(Country.SWITZERLAND).build();
    when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceSwitzerland);
    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint3.setDesignationOfficial("A Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
  }

    /**
     * Szenario 22: Haltestelle (stopPoint = true) in Status VALIDATED und No Geolocation, update mit Geolocation ändern in der Schweiz
     * <p>
     * NEU:       |________________Haltestelle A Hausen + Geolocation Switzerland____________|
     * <p>
     * IST:       |________________Haltestelle A Hausen + No Geolocation ____________________|
     * Status:                      VALIDATED
     * <p>
     * RESULTAT:  |________________Haltestelle A Hausen + Geolocation Switzerland____________|
     * Status:                      DRAFT
     */
    @Test
    void scenario22WhenStopPointWithNoGeolocationAndUpdateStopPointWithNewSwissGeolocationThenStopPointDraft() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        stopPoint1.setServicePointGeolocation(null);
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        // Check that status for no geolocation is validated
        assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
    }

    /**
     * Szenario 23: Haltestelle (stopPoint = true) in Status VALIDATED und Validity less than 60 days, update to Validity longer than 60 days
     * <p>
     * NEU:       |________________Haltestelle A Hausen + Validity longer than 60 days____________|
     * <p>
     * IST:       |________________Haltestelle A Hausen + Validity less than 60 days______________|
     * Status:                      VALIDATED
     * <p>
     * RESULTAT:  |________________Haltestelle A Hausen + Validity longer than 60 days____________|
     * Status:                      DRAFT
     */
    @Test
    void scenario23WhenStopPointWithValidityLessThan60DaysAndUpdateStopPointWithValidityLongerThan60DaysThenStopPointDraft() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2011, 1, 1));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        // Check that status for not enough long validity is validated
        assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
    }

    @Test
    void whenStopPointWithFrenchGeolocationAndUpdateStopPointWithNewSwissGeolocationAndNewNameThenStopPointDraft() throws Exception {
        GeoReference geoReferenceFrance = GeoReference.builder().country(Country.FRANCE).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceFrance);
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        // Check that status for french geolocation is validated
        assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

    GeoReference geoReferenceSwitzerland = GeoReference.builder().country(Country.SWITZERLAND).build();
    when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceSwitzerland);
    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint3.setDesignationOfficial("B Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString())));
  }

  @Test
  void whenStopPointWithStatusValidatedAndUpdateStopPointWithNewSwissGeolocationThenStopPointValidated() throws Exception {
    CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint1.setDesignationOfficial("A Hausen");
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        stopPoint1);
    Long id = servicePointVersionModel.getId();
    Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
    servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
    servicePointVersion1.ifPresent(repository::save);

    UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
    stopPoint3.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
    stopPoint3.setDesignationOfficial("A Hausen");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPoint3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
        .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())));
  }

    @Test
    void whenStopPointWithNoCountryGeolocationThenStopPointValidated() throws Exception {
        GeoReference geoReferenceCountryNull = GeoReference.builder().country(null).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceCountryNull);
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");

        mvc.perform(post("/v1/service-points")
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
    }

    @Test
    void whenStopPointWithNullGeolocationThenStopPointValidated() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setServicePointGeolocation(null);
        stopPoint1.setDesignationOfficial("A Hausen");

        mvc.perform(post("/v1/service-points")
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
    }

    @Test
    void whenStopPointWithGeolocationAbroadAndUpdateStopPointWithNullGeolocationThenStopPointValidated() throws Exception {
        GeoReference geoReferenceFrance = GeoReference.builder().country(Country.FRANCE).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceFrance);
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        // Check that status for french geolocation is validated
        assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

        GeoReference geoReferenceWithNullCountry = GeoReference.builder().country(Country.SWITZERLAND).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceWithNullCountry);
        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(null);
        stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())));
    }

    @Test
    void whenStopPointWithGeolocationAbroadAndUpdateStopPointWithNullCountryGeolocationThenStopPointValidated() throws Exception {
        GeoReference geoReferenceFrance = GeoReference.builder().country(Country.FRANCE).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceFrance);
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        // Check that status for french geolocation is validated
        assertThat(servicePointVersionModel.getStatus()).isEqualTo(Status.VALIDATED);

        GeoReference geoReferenceWithNullCountry = GeoReference.builder().country(null).build();
        when(geoReferenceService.getGeoReference(any(), anyBoolean())).thenReturn(geoReferenceWithNullCountry);
        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())));
    }

    @Test
    void whenStopPointWithStatusInReviewAndUpdateStopPointWithValidityChangeThenStopPointStaysInReview() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.IN_REVIEW));
        servicePointVersion1.ifPresent(repository::save);

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        stopPoint3.setValidTo(LocalDate.of(2016, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2016-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.IN_REVIEW.toString())));
    }

    @Test
    void whenStopPointWithStatusWithdrawnAndUpdateStopPointWithDesignationLongChangeThenStopPointStaysInReview() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint1.setDesignationOfficial("A Hausen");
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.WITHDRAWN));
        servicePointVersion1.ifPresent(repository::save);

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setServicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        stopPoint3.setValidTo(LocalDate.of(2015, 12, 31));
        stopPoint3.setDesignationOfficial("A Hausen");
        stopPoint3.setDesignationLong("designation long modified");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.WITHDRAWN.toString())));
    }

}
