package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        when(servicePointNumberService.getNextAvailableServicePointId(any())).thenReturn(1);
        servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
    }

    @AfterEach
    void cleanUpDb() {
        repository.deleteAll();
        fotCommentRepository.deleteAll();
    }

    @Test
    void scenario1WhenCreateNewStopPointThanSetStatusToInReview() throws Exception {
        mvc.perform(post("/v1/service-points")
                        .contentType(contentType)
                        .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
                .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString()))); // TODO: Here status should be InReview
    }

    @Test
    void scenario2WhenServicePointAndUpdateToStopPointThenStopPointInReview() throws Exception {
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        servicePoint.setMeansOfTransport(new ArrayList<>());
        servicePoint.setStopPointType(null);
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();
        Integer numberShort = servicePointVersionModel.getNumber().getNumberShort();

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
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString()))); // TODO: Change to InReview
    }

//    @Test
    void scenario3WhenThreeServicePointsAndUpdateToStopPointThenStopPointInReview() throws Exception { // TODO: this one doesn't work properly
        CreateServicePointVersionModel servicePoint1 = ServicePointTestData.getAargauServicePointVersionModel();
        servicePoint1.setMeansOfTransport(new ArrayList<>());
        servicePoint1.setStopPointType(null);
        servicePoint1.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePoint1.setValidTo(LocalDate.of(2011, 12, 31));
        servicePoint1.setDesignationLong("ABC1");
        servicePoint1.setCategories(List.of(Category.DISTRIBUTION_POINT));
        ReadServicePointVersionModel servicePointVersionModel1 = servicePointController.createServicePoint(
                servicePoint1);
        CreateServicePointVersionModel servicePoint2 = ServicePointTestData.getAargauServicePointVersionModel();
        servicePoint2.setMeansOfTransport(new ArrayList<>());
        servicePoint2.setStopPointType(null);
        servicePoint2.setValidFrom(LocalDate.of(2012, 1, 1));
        servicePoint2.setValidTo(LocalDate.of(2014, 12, 31));
        servicePoint2.setDesignationLong("ABC2");
        servicePoint2.setCategories(List.of(Category.MIGRATION_CENTRAL_SERVICE));
        ReadServicePointVersionModel servicePointVersionModel2 = servicePointController.createServicePoint(
                servicePoint2);
        CreateServicePointVersionModel servicePoint3 = ServicePointTestData.getAargauServicePointVersionModel();
        servicePoint3.setMeansOfTransport(new ArrayList<>());
        servicePoint3.setStopPointType(null);
        servicePoint3.setValidFrom(LocalDate.of(2015, 1, 1));
        servicePoint3.setValidTo(LocalDate.of(2019, 8, 10));
        servicePoint3.setDesignationLong("ABC3");
        servicePoint3.setCategories(List.of(Category.BORDER_POINT));
        ReadServicePointVersionModel servicePointVersionModel3 = servicePointController.createServicePoint(
                servicePoint3);
        Long id = servicePointVersionModel2.getId();

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
                .andExpect(jsonPath("$[3].status", is(Status.VALIDATED.toString()))); // TODO: Change to InReview
    }

    @Test
    void scenario4WhenStopPointAndChangeStopPointNameOnSecondPartThenStopPointWithNewNameInReview() throws Exception {
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();

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
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString()))); // TODO: Change to InReview
    }

    @Test
    void scenario5WhenStopPointAndChangeStopPointNameOnFirstPartThenStopPointWithNewNameInReview() throws Exception {
        CreateServicePointVersionModel servicePoint = ServicePointTestData.getAargauServicePointVersionModel();
        ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
                servicePoint);
        Long id = servicePointVersionModel.getId();

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
                .andExpect(jsonPath("$[0].status", is(Status.VALIDATED.toString()))) // TODO: Change to InReview
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2016-01-01")))
                .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
                .andExpect(jsonPath("$[1].status", is(Status.VALIDATED.toString())));
    }



}
