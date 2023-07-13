package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrafficPointElementControllerApiTest extends BaseControllerApiTest {

  private final TrafficPointElementVersionRepository repository;
  private TrafficPointElementVersion trafficPointElementVersion;

  @Autowired
  public TrafficPointElementControllerApiTest(TrafficPointElementVersionRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void createDefaultVersion() {
    TrafficPointElementGeolocation trafficPointElementGeolocation = TrafficPointElementGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2505236.389)
        .north(1116323.213)
        .height(-9999.0)
        .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .editor("fs45117")
        .build();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("gali00")
        .servicePointNumber(ServicePointNumber.of(14000158))
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .sloid("ch:1:sloid:1400015:0:310240")
        .compassDirection(277.0)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 6))
        .validTo(LocalDate.of(2099, 12, 31))
        .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
        .editor("fs45117")
        .build();
    trafficPointElementGeolocation.setTrafficPointElementVersion(trafficPointElementVersion);

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
    mvc.perform(post("/v1/traffic-point-elements")
            .contentType(contentType)
            .content(mapper.writeValueAsString(TrafficPointTestData.getBasicTrafficPointWithoutGeolocation())))
        .andExpect(status().isCreated());
  }

}
