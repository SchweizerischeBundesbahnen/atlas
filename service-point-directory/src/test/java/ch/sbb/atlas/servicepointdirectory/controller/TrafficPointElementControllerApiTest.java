package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.model.TestData;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrafficPointElementControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private TrafficPointElementValidationService trafficPointElementValidationService;

  private final TrafficPointElementVersionRepository repository;
  private TrafficPointElementVersion trafficPointElementVersion;
  private ServicePointVersionRepository servicePointVersionRepository;
  private ServicePointVersion servicePointVersion;

  private final TrafficPointElementController trafficPointElementController;

  @Autowired
  public TrafficPointElementControllerApiTest(TrafficPointElementVersionRepository repository,
                                              TrafficPointElementController trafficPointElementController,
                                              ServicePointVersionRepository servicePointVersionRepository) {
    this.repository = repository;
    this.trafficPointElementController = trafficPointElementController;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    this.trafficPointElementVersion = repository.save(trafficPointElementVersion);

    servicePointVersion = TestData.testServicePointForTrafficPoint();
    this.servicePointVersion = servicePointVersionRepository.save(servicePointVersion);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldGetTrafficPointElement() throws Exception {
    mvc.perform(get("/v1/traffic-point-elements/" + trafficPointElementVersion.getSloid())).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(trafficPointElementVersion.getId().intValue())))
        .andExpect(jsonPath("$[0]." + Fields.sloid, is("ch:1:sloid:1400015:0:310240")))
        .andExpect(jsonPath("$[0]." + Fields.designationOperational, is("gali00")))
        .andExpect(jsonPath("$[0].hasGeolocation", is(true)))
        .andExpect(jsonPath("$[0].creationDate", is("2019-12-06T08:02:34")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldGetTrafficPointElementVersions() throws Exception {
    mvc.perform(get("/v1/traffic-point-elements")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + Fields.id, is(trafficPointElementVersion.getId().intValue())))
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldGetTrafficPointElementVersionById() throws Exception {
    mvc.perform(get("/v1/traffic-point-elements/versions/" + trafficPointElementVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldFailOnInvalidTrafficPointElementNumber() throws Exception {
    mvc.perform(get("/v1/traffic-point-elements/123"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldImportTrafficPointsSuccessfully() throws Exception {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/VERKEHRSPUNKTELEMENTE_VERSIONING.csv")) {
      // given
      List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(
          csvStream);
      List<TrafficPointElementCsvModel> trafficPointElementCsvModelsOrderedByValidFrom
          = trafficPointElementCsvModels.stream()
          .sorted(Comparator.comparing(BaseDidokCsvModel::getValidFrom))
          .toList();
      String sloid = trafficPointElementCsvModels.get(0).getSloid();
      TrafficPointImportRequestModel importRequestModel = new TrafficPointImportRequestModel(
          List.of(
              TrafficPointCsvModelContainer
                  .builder()
                  .trafficPointCsvModelList(trafficPointElementCsvModelsOrderedByValidFrom)
                  .sloid(sloid)
                  .build()
          )
      );
      String jsonString = mapper.writeValueAsString(importRequestModel);

      // when
      mvc.perform(post("/v1/traffic-point-elements/import")
              .content(jsonString)
              .contentType(contentType))
          // then
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)));
    }
  }

  @Test
  void shouldReturnBadRequestOnEmptyListImportRequest() throws Exception {
    // given
    TrafficPointImportRequestModel importRequestModel = new TrafficPointImportRequestModel(
        Collections.emptyList()
    );
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/traffic-point-elements/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullListImportRequest() throws Exception {
    // given
    TrafficPointImportRequestModel importRequestModel = new TrafficPointImportRequestModel();
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/traffic-point-elements/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullImportRequestModel() throws Exception {
    // given & when
    mvc.perform(post("/v1/traffic-point-elements/import")
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateTrafficPointElement() throws Exception {
    repository.deleteAll();
    mvc.perform(post("/v1/traffic-point-elements")
            .contentType(contentType)
            .content(mapper.writeValueAsString(TrafficPointTestData.getCreateTrafficPointVersionModel())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + TrafficPointElementVersion.Fields.id, is(trafficPointElementVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.servicePointNumber.number", is(1400015)))
        .andExpect(jsonPath("$.servicePointNumber.numberShort", is(15)))
        .andExpect(jsonPath("$.servicePointNumber.uicCountryCode", is(14)))
        .andExpect(jsonPath("$.servicePointNumber.checkDigit", is(8)))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.designation, is(trafficPointElementVersion.getDesignation())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.designationOperational, is(trafficPointElementVersion.getDesignationOperational())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.length, is(trafficPointElementVersion.getLength())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.boardingAreaHeight, is(trafficPointElementVersion.getBoardingAreaHeight())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.compassDirection, is(trafficPointElementVersion.getCompassDirection())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.sloid, is(trafficPointElementVersion.getSloid())))
        .andExpect(jsonPath("$."+ TrafficPointElementVersion.Fields.parentSloid, is(trafficPointElementVersion.getParentSloid())))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.lv95.north", is(5811120.069385619)))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.lv95.east", is(691419.9033588431)))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.wgs84.north", is(76.16830524895504)))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.wgs84.east", is(-67.70653042926492)))
        .andExpect(jsonPath("$.trafficPointElementGeolocation.height", is(-9999.0)));
  }

  @Test
  public void shouldUpdateTrafficPointAndCreateMultipleVersions() throws Exception {
    repository.deleteAll();
    ReadTrafficPointElementVersionModel trafficPointElementVersionModel = trafficPointElementController.createTrafficPoint(
            TrafficPointTestData.getCreateTrafficPointVersionModel());
    Long id = trafficPointElementVersionModel.getId();

    CreateTrafficPointElementVersionModel newTrafficPointVersionModel = TrafficPointTestData.getCreateTrafficPointVersionModel();
    newTrafficPointVersionModel.setTrafficPointElementGeolocation(
            GeolocationMapper.toModel(TrafficPointTestData.getTrafficPointGeolocationBernMittelland()));
    newTrafficPointVersionModel.setValidFrom(LocalDate.of(2021, 1, 1));
    newTrafficPointVersionModel.setValidTo(LocalDate.of(2021, 12, 31));

    mvc.perform(MockMvcRequestBuilders.put("/v1/traffic-point-elements/" + id)
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(newTrafficPointVersionModel)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0]." + TrafficPointElementVersion.Fields.id, is(trafficPointElementVersion.getId().intValue() + 1)))
            .andExpect(jsonPath("$[0].servicePointNumber.number", is(1400015)))
            .andExpect(jsonPath("$[0].servicePointNumber.numberShort", is(15)))
            .andExpect(jsonPath("$[0].servicePointNumber.uicCountryCode", is(14)))
            .andExpect(jsonPath("$[0].servicePointNumber.checkDigit", is(8)))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.designation, is(trafficPointElementVersion.getDesignation())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.designationOperational, is(trafficPointElementVersion.getDesignationOperational())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.length, is(trafficPointElementVersion.getLength())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.boardingAreaHeight, is(trafficPointElementVersion.getBoardingAreaHeight())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.compassDirection, is(trafficPointElementVersion.getCompassDirection())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.sloid, is(trafficPointElementVersion.getSloid())))
            .andExpect(jsonPath("$[0]."+ TrafficPointElementVersion.Fields.parentSloid, is(trafficPointElementVersion.getParentSloid())))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.spatialReference", is(LV95.toString())))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.lv95.north", is(5811120.069385619)))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.lv95.east", is(691419.9033588431)))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.wgs84.north", is(76.16830524895504)))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.wgs84.east", is(-67.70653042926492)))
            .andExpect(jsonPath("$[0].trafficPointElementGeolocation.height", is(-9999.0)))
            .andExpect(jsonPath("$[1]." + TrafficPointElementVersion.Fields.id, is(trafficPointElementVersion.getId().intValue() + 2)))
            .andExpect(jsonPath("$[1].servicePointNumber.number", is(1400015)))
            .andExpect(jsonPath("$[1].servicePointNumber.numberShort", is(15)))
            .andExpect(jsonPath("$[1].servicePointNumber.uicCountryCode", is(14)))
            .andExpect(jsonPath("$[1].servicePointNumber.checkDigit", is(8)))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.designation, is(trafficPointElementVersion.getDesignation())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.designationOperational, is(trafficPointElementVersion.getDesignationOperational())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.length, is(trafficPointElementVersion.getLength())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.boardingAreaHeight, is(trafficPointElementVersion.getBoardingAreaHeight())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.compassDirection, is(trafficPointElementVersion.getCompassDirection())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.sloid, is(trafficPointElementVersion.getSloid())))
            .andExpect(jsonPath("$[1]."+ TrafficPointElementVersion.Fields.parentSloid, is(trafficPointElementVersion.getParentSloid())))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.spatialReference", is(LV95.toString())))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.lv95.north", is(5935706.6515024565)))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.lv95.east", is(829210.4077282187)))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.wgs84.north", is(77.40956704901323)))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.wgs84.east", is(-69.39759684060752)))
            .andExpect(jsonPath("$[1].trafficPointElementGeolocation.height", is(555.98)))
            .andExpect(jsonPath("$[2]." + TrafficPointElementVersion.Fields.id, is(trafficPointElementVersion.getId().intValue() + 3)))
            .andExpect(jsonPath("$[2].servicePointNumber.number", is(1400015)))
            .andExpect(jsonPath("$[2].servicePointNumber.numberShort", is(15)))
            .andExpect(jsonPath("$[2].servicePointNumber.uicCountryCode", is(14)))
            .andExpect(jsonPath("$[2].servicePointNumber.checkDigit", is(8)))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.designation, is(trafficPointElementVersion.getDesignation())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.designationOperational, is(trafficPointElementVersion.getDesignationOperational())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.length, is(trafficPointElementVersion.getLength())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.boardingAreaHeight, is(trafficPointElementVersion.getBoardingAreaHeight())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.compassDirection, is(trafficPointElementVersion.getCompassDirection())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.sloid, is(trafficPointElementVersion.getSloid())))
            .andExpect(jsonPath("$[2]."+ TrafficPointElementVersion.Fields.parentSloid, is(trafficPointElementVersion.getParentSloid())))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.spatialReference", is(LV95.toString())))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.lv95.north", is(5811120.069385619)))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.lv95.east", is(691419.9033588431)))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.wgs84.north", is(76.16830524895504)))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.wgs84.east", is(-67.70653042926492)))
            .andExpect(jsonPath("$[2].trafficPointElementGeolocation.height", is(-9999.0)));
  }


  @Test
  public void shouldUpdateTrafficPointAndNotCreateMultipleVersions() throws Exception {
    repository.deleteAll();
    ReadTrafficPointElementVersionModel trafficPointElementVersionModel = trafficPointElementController
            .createTrafficPoint(TrafficPointTestData.getCreateTrafficPointVersionModel());
    Long id = trafficPointElementVersionModel.getId();

    CreateTrafficPointElementVersionModel newTrafficPointVersionModel = TrafficPointTestData.getCreateTrafficPointVersionModel();
    newTrafficPointVersionModel.setTrafficPointElementGeolocation(
            GeolocationMapper.toModel(TrafficPointTestData.getTrafficPointGeolocationBernMittelland()));
    mvc.perform(MockMvcRequestBuilders.put("/v1/traffic-point-elements/" + id)
             .contentType(contentType)
             .content(mapper.writeValueAsString(newTrafficPointVersionModel)))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void shouldThrowSloidsNotEqualExceptionWhenUpdate() throws Exception {
    repository.deleteAll();
    // given
    CreateTrafficPointElementVersionModel newTrafficPointElementVersionModel = TrafficPointTestData.getCreateTrafficPointVersionModel();
    ReadTrafficPointElementVersionModel savedTrafficPointElementVersionModel = trafficPointElementController
            .createTrafficPoint(newTrafficPointElementVersionModel);
    newTrafficPointElementVersionModel.setId(savedTrafficPointElementVersionModel.getId());
    newTrafficPointElementVersionModel.setSloid("ch:1:sloid:1400015:0:310241");

    // when
    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/v1/traffic-point-elements/" + savedTrafficPointElementVersionModel.getId())
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(newTrafficPointElementVersionModel)))
            .andExpect(status().isBadRequest()).andReturn();

    // then
    ErrorResponse errorResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(errorResponse.getMessage()).isEqualTo("Sloid for provided id: ch:1:sloid:1400015:0:310240 and sloid in the request body: ch:1:sloid:1400015:0:310241 are not equal.");

  }

  @Test
  public void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    repository.deleteAll();
    // given
    CreateTrafficPointElementVersionModel createTrafficPointElementVersionModel = TrafficPointTestData.getCreateTrafficPointVersionModel();
    ReadTrafficPointElementVersionModel savedTrafficPointElementVersionModel = trafficPointElementController
    .createTrafficPoint(createTrafficPointElementVersionModel);

    // when first update it is ok
    createTrafficPointElementVersionModel.setId(savedTrafficPointElementVersionModel.getId());
    createTrafficPointElementVersionModel.setSloid(savedTrafficPointElementVersionModel.getSloid());
    createTrafficPointElementVersionModel.setEtagVersion(savedTrafficPointElementVersionModel.getEtagVersion());

    createTrafficPointElementVersionModel.setDesignationOperational("1 designation");
    mvc.perform(MockMvcRequestBuilders.put("/v1/traffic-point-elements/" + createTrafficPointElementVersionModel.getId())
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(createTrafficPointElementVersionModel)))
            .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    createTrafficPointElementVersionModel.setDesignationOperational("2 designation");
    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/v1/traffic-point-elements/" + createTrafficPointElementVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(createTrafficPointElementVersionModel)))
            .andExpect(status().isPreconditionFailed()).andReturn();

    ErrorResponse errorResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
    assertThat(errorResponse.getError()).isEqualTo("Stale object state error");
  }

}
