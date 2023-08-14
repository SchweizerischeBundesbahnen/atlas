package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoadingPointControllerApiTest extends BaseControllerApiTest {

  private final LoadingPointVersionRepository repository;

  private final ServicePointVersionRepository servicePointVersionRepository;
  private LoadingPointVersion loadingPointVersion;

  private ServicePointVersion servicePointVersion;

  @Autowired
  public LoadingPointControllerApiTest(LoadingPointVersionRepository repository, ServicePointVersionRepository servicePointVersionRepository) {
    this.repository = repository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    LoadingPointGeolocation loadingPointGeolocation = LoadingPointGeolocation
        .builder()
        .spatialReference(LV95)
        .east(2506426.604)
        .north(1116455.883)
        .height(-9999.0)
        .creator("fs45117")
        .creationDate(LocalDateTime.of(2017, 12, 4, 13, 11, 3))
        .editor("GSU_DIDOK")
        .editionDate(LocalDateTime.of(2018, 6, 28, 11, 48, 56))
        .build();

    servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.createAbroadServicePointVersion());

    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(4201)
        .designation("Piazzale")
        .designationLong("Piazzaleee")
        .connectionPoint(false)
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2018, 6, 28))
        .validTo(LocalDate.of(2099, 12, 31))
        .creator("fs45117")
        .creationDate(LocalDateTime.of(2017, 12, 4, 13, 11, 3))
        .editor("GSU_DIDOK")
        .editionDate(LocalDateTime.of(2018, 6, 28, 11, 48, 56))
        .loadingPointGeolocation(loadingPointGeolocation)
        .build();

    loadingPointGeolocation.setLoadingPointVersion(loadingPointVersion);

    this.loadingPointVersion = repository.save(loadingPointVersion);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldGetLoadingPoint() throws Exception {
    int servicePointNumber = servicePointVersion.getNumber().getValue();
    Integer number = loadingPointVersion.getNumber();
    mvc.perform(get("/v1/loading-points/"+servicePointNumber+"/"+ number)).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(loadingPointVersion.getId().intValue())))
        .andExpect(jsonPath("$[0]." + Fields.number, is(4201)))
        .andExpect(jsonPath("$[0]." + Fields.connectionPoint, is(false)))
        .andExpect(jsonPath("$[0].servicePointNumber.number", is(ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber).getNumber())))
        .andExpect(jsonPath("$[0].hasGeolocation", is(true)))
        .andExpect(jsonPath("$[0].loadingPointGeolocation.lv95.north", is(1116455.883)))
        .andExpect(jsonPath("$[0].creationDate", is("2017-12-04T13:11:03")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldGetLoadingPointVersions() throws Exception {
    mvc.perform(get("/v1/loading-points"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0]." + Fields.id, is(loadingPointVersion.getId().intValue())));
  }

  @Test
  void shouldGetLoadingPointVersionById() throws Exception {
    mvc.perform(get("/v1/loading-points/versions/" + loadingPointVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldFailOnInvalidLoadingPointNumber() throws Exception {
    mvc.perform(get("/v1/loading-points/9123"))
        .andExpect(status().isNotFound());
  }

}