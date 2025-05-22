package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import ch.sbb.atlas.servicepointdirectory.config.OAuthFeignConfig;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.JourneyPoiClientBase;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class StopPointTerminationControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private JourneyPoiConfig journeyPoiConfig;

  @MockitoBean
  private OAuthFeignConfig oAuthFeignConfig;

  @MockitoBean
  private JourneyPoiClientBase journeyPoiClient;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  private final ServicePointVersionRepository repository;
  private final ServicePointController servicePointController;
  private ServicePointVersion servicePointVersion;

  @Autowired
  StopPointTerminationControllerApiTest(ServicePointVersionRepository repository, ServicePointController servicePointController) {
    this.repository = repository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());

    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:1");
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldStopServicePointTermination() throws Exception {
    ServicePointVersion servicePointVersion = ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
    servicePointVersion.setStatus(Status.VALIDATED);
    ServicePointVersion version = repository.save(servicePointVersion);
    Long id = version.getId();
    String sloid = version.getSloid();

    mvc.perform(put("/v1/service-points/termination/stop/" + sloid + "/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terminationInProgress", is(false)));
  }

  @Test
  void shouldNotStopServicePointTerminationWhenIdNotFound() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    long id = 456L;
    String sloid = servicePointVersionModel.getSloid();

    mvc.perform(put("/v1/service-points/termination/stop/" + sloid + "/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldNotStopServicePointTerminationWhenSloidDoesNotExists() throws Exception {
    long id = 123L;
    String sloid = "ch:1:sloid:753126";

    mvc.perform(put("/v1/service-points/termination/stop/" + sloid + "/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldStartServicePointTermination() throws Exception {
    ServicePointVersion servicePointVersion = ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
    servicePointVersion.setStatus(Status.VALIDATED);
    ServicePointVersion version = repository.save(servicePointVersion);
    Long id = version.getId();
    String sloid = version.getSloid();

    UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(true)
        .terminationDate(version.getValidTo().minusDays(1))
        .build();

    mvc.perform(put("/v1/service-points/termination/start/" + sloid + "/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terminationInProgress", is(true)));
  }

  @Test
  void shouldNotStartServicePointTerminationWhenIdNotFound() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    long id = 456L;
    String sloid = servicePointVersionModel.getSloid();

    UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(true)
        .terminationDate(servicePointVersionModel.getValidTo().minusDays(1))
        .build();
    mvc.perform(put("/v1/service-points/termination/start/" + sloid + "/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldNotStartServicePointTerminationWhenSloidDoesNotExists() throws Exception {
    long id = 123L;
    String sloid = "ch:1:sloid:753126";

    UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(true)
        .terminationDate(LocalDate.now())
        .build();

    mvc.perform(put("/v1/service-points/termination/start/" + sloid + "/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
        .andExpect(status().isNotFound());
  }

}
