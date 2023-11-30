package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicePointStopPointApiScenariosTest extends BaseControllerApiTest {

    @MockBean
    private SharedBusinessOrganisationService sharedBusinessOrganisationService;
    @MockBean
    private ServicePointNumberService servicePointNumberService;
    @MockBean
    private GeoReferenceService geoReferenceService;

    private final ServicePointVersionRepository repository;
    private final ServicePointFotCommentRepository fotCommentRepository;
    private final ServicePointController servicePointController;
    private ServicePointVersion servicePointVersion;

    @Autowired
    ServicePointStopPointApiScenariosTest(ServicePointVersionRepository repository,
                                  ServicePointFotCommentRepository fotCommentRepository, ServicePointController servicePointController) {
        this.repository = repository;
        this.fotCommentRepository = fotCommentRepository;
        this.servicePointController = servicePointController;
    }

    @BeforeEach
    void createDefaultVersion() {
        GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
        when(geoReferenceService.getGeoReference(any())).thenReturn(geoReference);
        when(servicePointNumberService.getNextAvailableServicePointId(any())).thenReturn(1);
    }

    @AfterEach
    void cleanUpDb() {
        repository.deleteAll();
        fotCommentRepository.deleteAll();
    }

    /**
     * Szenario 1: Neue Haltestelle erfassen
     * NEU:  |________________Haltestelle_________________|
     * IST:
     * Version:
     *
     * RESULTAT:  |________________Haltestelle_________________|
     * Version:
     * Status:    DRAFT
     */

    @Test
    void scenario1WhenCreateNewStopPointThanSetStatusToDRAFT() throws Exception {
        servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
        mvc.perform(post("/v1/service-points")
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
                .andExpect(jsonPath("$.status", is(Status.DRAFT.toString()))); // TODO: Here status should be DRAFT
    }

    /**
     * Szenario 2: Update von Dienststelle auf Haltestelle
     * NEU:                                |________________Haltestelle_________________|
     * IST:       |_________________________________Dienststelle_____________________________|
     * Version:
     *
     * RESULTAT:  |_________Dienststelle___|________Haltestelle_____________________________|
     * Version:
     * Status:       VALIDATED                             DRAFT
     */

    @Test
    void scenario2WhenServicePointAndUpdateToStopPointThenStopPointDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        servicePoint.setMeansOfTransport(new ArrayList<>());
        servicePoint.setStopPointType(null);
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();
        Integer numberShort = servicePointVersionModel.getNumber().getNumberShort();

        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

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
                .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 3: Update von Dienststelle auf Haltestelle
     * NEU:                                                               |________________Haltestelle____________________________|
     * IST:       |__________Dienststelle___________|____________Dienststelle__________________|___________Dienststelle___________|
     * Version:
     *
     * RESULTAT:  |__________Dienststelle___________|__Dienststelle_______|____Haltestelle_____|___Haltestelle____________________|
     * Version:
     * Status:       VALIDATED                             VALIDATED            DRAFT             DRAFT
     */
//    @Test
    void scenario3WhenThreeServicePointsAndUpdateToStopPointThenStopPointDRAFT() throws Exception { // TODO: this one doesn't work properly
        repository.deleteAll();
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
        List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(servicePointVersionModel1.getId(),
                servicePoint3);
        Long id = servicePointVersionModels.get(2).getId();
        servicePointController.getServicePointVersions(servicePointVersionModels.get(0).getNumber().getNumber());

        UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
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
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())))
                .andExpect(jsonPath("$[3]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
                .andExpect(jsonPath("$[3]." + ServicePointVersionModel.Fields.validTo, is("2019-8-10")))
                .andExpect(jsonPath("$[3].status", is(Status.VALIDATED.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 4: Update von Haltestelle, Namensänderung
     * NEU:                               |________________Haltestelle B Hausen________|
     * IST:       |________________________________Haltestelle A Hausen________________|
     * Version:
     *
     * RESULTAT:  |__Haltestelle A Hausen__|__________Haltestelle B Hausen_____________|
     * Version:
     * Status:       VALIDATED                              DRAFT
     */
    @Test
    void scenario4WhenStopPointAndChangeStopPointNameOnSecondPartThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();

        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint.setDesignationOfficial("Bern Strasse");
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
                .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 5: Update von Haltestelle, Namensänderung
     * NEU:       |________________Haltestelle B Hausen________|
     * IST:       |________________________________Haltestelle A Hausen________________|
     * Version:
     *
     * RESULTAT:  |__________Haltestelle B Hausen_____________|__Haltestelle A Hausen__|
     * Version:
     * Status:                DRAFT                               VALIDATED
     */
    @Test
    void scenario5WhenStopPointAndChangeStopPointNameOnFirstPartThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();

        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        UpdateServicePointVersionModel stopPoint = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint.setDesignationOfficial("Bern Strasse");
        stopPoint.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint.setValidTo(LocalDate.of(2015, 12, 31));

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString()))) // TODO: Change to DRAFT
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
    }

    /**
     * Szenario 6: Update von Haltestelle, Namensänderung
     * NEU:                                                                             |__Haltestelle C Hausen__|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen________|__Haltestelle B Hausen__|__Haltestelle C Hausen__|
     * Version:
     * Status:                VALIDATED                                VALIDATED                DRAFT
     */
    @Test
    void scenario6WhenTwoStopPointsAndChangeStopPointNameOnLastPartThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
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
                .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString())));// TODO: Change to DRAFT
    }

    /**
     * Szenario 7: Update von Haltestelle, Namensänderung
     * NEU:                                         |__Haltestelle C Hausen__|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |_____Haltestelle A Hausen________|__Haltestelle C Hausen__|_______Haltestelle B Hausen________|
     * Version:
     * Status:                VALIDATED                        DRAFT                    VALIDATED
     */
    @Test
    void scenario7WhenTwoStopPointsAndChangeStopPointNameInTheMiddleThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2015, 1, 1));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2016, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
        stopPoint3.setValidFrom(LocalDate.of(2015, 6, 1));
        stopPoint3.setValidTo(LocalDate.of(2016, 6, 1));

        mvc.perform(put("/v1/service-points/" + id1)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2015-05-31")))
                .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-06-01")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2016-06-01")))
                .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString())))// TODO: Change to DRAFT
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-06-02")))
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2016-12-31")))
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
    }

    /**
     * Szenario 8: Update von Haltestelle, Namensänderung
     * NEU:                                |_______Haltestelle B Hausen_______|
     * IST:       |__________________________Haltestelle A Hausen________________________________________________|
     * Version:
     *
     * RESULTAT:  |__Haltestelle A Hausen__|_______Haltestelle B Hausen_______|________Haltestelle A Hausen______|
     * Version:
     * Status:                VALIDATED                        DRAFT                    VALIDATED
     */
    @Test
    void scenario8WhenStopPointAndChangeStopPointNameInTheMiddleThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
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
                .andExpect(jsonPath("$[1].status", is(Status.DRAFT.toString()))) // TODO: Change to DRAFT
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2017-01-01")))
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
    }

    /**
     * Szenario 9: Update von Haltestelle, Namensänderung
     * NEU:       |__________________________Haltestelle B Hausen________________________________________________|
     * IST:       |__________________________Haltestelle A Hausen________________________________________________|
     * Version:
     *
     * RESULTAT:  |__________________________Haltestelle B Hausen________________________________________________|
     * Version:
     * Status:                                   DRAFT
     */
    @Test
    void scenario9WhenStopPointAndChangeStopPointNameThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 10: Update von Haltestelle, Namensänderung + Koordinatensänderung
     * NEU:                                                                                                   |___Haltestelle B Hausen + Koordinaten 3 _____|
     * IST:       |________________Haltestelle A Hausen + Koordinaten 1________|________________________________Haltestelle A Hausen + Koordinaten 2________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen + Koordinaten 1________|_HS A Hausen + Koordinaten 2__|___Haltestelle B Hausen + Koordinaten 3 _____|
     * Version:
     * Status:                                VALIDATED                                    VALIDATED                           DRAFT
     */
    @Test
    void scenario10WhenTwoStopPointsWith2CoordinatesAndChangeStopPointNameAndCoordinateAtTheEndThenStopPointWithNewNameDRAFT() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        stopPoint2.setDesignationOfficial("Blublo");
//        stopPoint2.setServicePointGeolocation(
//                ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        List<ReadServicePointVersionModel> servicePointVersionModel1 = servicePointController.updateServicePoint(id,
                stopPoint2);
        Long id1 = servicePointVersionModel1.get(1).getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        servicePointVersion2.get().setDesignationOfficial("Aargau Strasse");
        servicePointVersion2.get().setServicePointGeolocation(ServicePointTestData.getAargauServicePointGeolocation());
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
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
                .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString()))); // TODO: Change to DRAFT
    }


    /**
     * Szenario 11: Update von Haltestelle, Namensänderung + Koordinatensänderung
     * NEU:                                              |_______________________________Haltestelle B Hausen + Koordinaten 3 _______________________________________________|
     * IST:       |___________________________Haltestelle A Hausen + Koordinaten 1______________|________________________________Haltestelle A Hausen + Koordinaten 2________|
     * Version:
     *
     * RESULTAT:  |_Haltestelle A Hausen + Koordinaten 1_|_Haltestelle B Hausen + Koordinaten 3_|____________Haltestelle A Hausen + Koordinaten 1____________________________|
     * Version:
     * Status:                                VALIDATED                                    VALIDATED                           DRAFT
     */
//    @Test // TODO: Check why is not working
    void scenario11WhenTwoStopPointsWith2CoordinatesAndChangeStopPointNameAndCoordinateThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint1);

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        stopPoint2.setServicePointGeolocation(
                ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
        ReadServicePointVersionModel servicePointVersionModel2 = servicePointController.createServicePoint(
                stopPoint2);
        Long id = servicePointVersionModel2.getId();

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
        stopPoint3.setValidFrom(LocalDate.of(2015, 1, 1));
        stopPoint3.setValidTo(LocalDate.of(2019, 8, 10));
        stopPoint3.setServicePointGeolocation(
                ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getZurichServicePointGeolocation()));

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2014-12-31")))
                .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString())))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2015-01-01")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString()))) // TODO: Change to DRAFT
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 12: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:                                                                                                      |__Verlängerung & Wechseln C Hausen__|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen_________|__Verlängerung & Wechseln C Hausen__|
     * Version:
     * Status:                        VALIDATED                                           VALIDATED                          DRAFT
     */
    @Test
    void scenario12WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
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
                .andExpect(jsonPath("$[2].status", is(Status.DRAFT.toString()))); // TODO: Change to DRAFT
    }

    /**
     * Szenario 13: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:       |__Verlängerung & Wechseln C Hausen__|
     * IST:                                            |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |__Verlängerung & Wechseln C Hausen__|________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen_________|
     * Version:
     * Status:                 DRAFT                                  VALIDATED                                           VALIDATED
     */
    @Test
    void scenario13WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");
        stopPoint3.setValidFrom(LocalDate.of(2005, 8, 11));
        stopPoint3.setValidTo(LocalDate.of(2010, 12, 10));

        mvc.perform(put("/v1/service-points/" + id)
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(stopPoint3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2005-08-11")))
                .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2010-12-10")))
                .andExpect(jsonPath("$[0].status", is(Status.DRAFT.toString()))) // TODO: Change to DRAFT
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2015-12-31")))
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
                .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
    }

    /**
     * Szenario 14: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:                                                                                                      |__Verlängerung B Hausen__|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen__________________________________|
     * Version:
     * Status:                      VALIDATED                                          VALIDATED
     */
    @Test
    void scenario14WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointValidated() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Bern Strasse");
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
     * Szenario 15: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:       |__Verlängerung A Hausen__|
     * IST:                                 |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen__________________________________|_____________________Haltestelle B Hausen________|
     * Version:
     * Status:                         VALIDATED                                                           VALIDATED
     */
    @Test
    void scenario15WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
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
     * Szenario 16: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:                                               |_______________________Wechsel zu B Hausen____________|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen___|__________________________Haltestelle B Hausen________|
     * Version:
     * Status:                         VALIDATED                                   VALIDATED
     */
    @Test
    void scenario16WhenTwoStopPointsWith2NamesAndStopPointUpdateWithoutNameChangeThenStopPointValidated() throws Exception {
        repository.deleteAll();
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Bern Strasse");
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
     * Szenario 17: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:       |_______________________Wechsel zu A Hausen____________|
     * IST:       |________________Haltestelle A Hausen________|_____________________Haltestelle B Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen__________________|___________Haltestelle B Hausen________|
     * Version:
     * Status:                         VALIDATED                                   VALIDATED
     */
    @Test
    void scenario17WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2019, 8, 10));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id1 = servicePointVersionModel1.getId();
        Optional<ServicePointVersion> servicePointVersion2 = repository.findById(id1);
        servicePointVersion2.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion2.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Aargau Strasse");
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
                .andExpect(jsonPath("$[0].designationOfficial", is("Aargau Strasse")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2017-12-11")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())))
                .andExpect(jsonPath("$[1].designationOfficial", is("Bern Strasse")));
    }

    /**
     * Szenario 18: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:                                                                    |________________Wiedereinführung & Wechsel zu B Hausen____________|
     * IST:       |________________Haltestelle A Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen_________|              |________________Wiedereinführung & Wechsel zu B Hausen____________|
     * Version:
     * Status:                         VALIDATED                                                              DRAFT
     */
    @Test
    void scenario18WhenTwoStopPointsWith2NamesAndStopPointUpdateWithoutNameChangeThenStopPointValidated() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Bern Strasse");
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
     * Szenario 19: Verlängerung und Wechseln zu C Haltestelle. Annahme: wir sprechen immer von Versionen mit stopPoint = true
     * Dargestellt nur die Fälle in denen ausschliesslich der angepasst wird. Werden noch weitere Attribute angepasst (z.B. Koordinaten, Kategorie)
     * bleibt das Prinzip gleich, es wird jedoch entsprechend mehr Versionen geben
     * NEU:                                                                    |________________Wiedereinführung & Wechsel zu A Hausen____________|
     * IST:       |________________Haltestelle A Hausen________|
     * Version:
     *
     * RESULTAT:  |________________Haltestelle A Hausen_________|              |________________Wiedereinführung & Wechsel zu A Hausen____________|
     * Version:
     * Status:                         VALIDATED                                                              DRAFT
     */
    @Test
    void scenario19WhenTwoStopPointsWith2NamesAndStopPointUpdateWithoutNameChangeThenStopPointValidated() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);
        Long id = servicePointVersionModel.getId();
        Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
        servicePointVersion1.get().setStatus(Status.VALIDATED);
        repository.save(servicePointVersion1.get());

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
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

//    @Test // TODO: Fix failing test
    void scenario20WhenTwoStopPointsWith2NamesAndStopPointExtendsThenStopPointWithNewNameDRAFT() throws Exception {
        CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint1.setValidTo(LocalDate.of(2015, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                stopPoint1);

        CreateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint2.setDesignationOfficial("Bern Strasse");
        stopPoint2.setValidFrom(LocalDate.of(2016, 1, 1));
        stopPoint2.setValidTo(LocalDate.of(2017, 12, 31));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                stopPoint2);
        Long id = servicePointVersionModel1.getId();

        UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        stopPoint3.setDesignationOfficial("Zurich Strasse");

        mvc.perform(put("/v1/service-points/" + id)
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
                .andExpect(jsonPath("$[2].status", is(Status.VALIDATED.toString())));
    }



}
