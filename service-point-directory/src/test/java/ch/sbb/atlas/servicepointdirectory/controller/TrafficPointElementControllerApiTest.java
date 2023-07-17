package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrafficPointElementControllerApiTest extends BaseControllerApiTest {

  private final TrafficPointElementVersionRepository repository;
  private TrafficPointElementVersion trafficPointElementVersion;

  private final TrafficPointElementController trafficPointElementController;

  @Autowired
  public TrafficPointElementControllerApiTest(TrafficPointElementVersionRepository repository, TrafficPointElementController trafficPointElementController) {
    this.repository = repository;
    this.trafficPointElementController = trafficPointElementController;
  }

  @BeforeEach
  void createDefaultVersion() {
    trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();

    this.trafficPointElementVersion = repository.save(trafficPointElementVersion);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
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

}
