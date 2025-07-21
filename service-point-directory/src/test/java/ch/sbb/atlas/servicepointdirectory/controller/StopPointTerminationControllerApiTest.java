package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import ch.sbb.atlas.servicepointdirectory.config.OAuthFeignConfig;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.JourneyPoiClientBase;
import java.time.LocalDate;
import java.util.List;
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

  @Autowired
  StopPointTerminationControllerApiTest(ServicePointVersionRepository repository, ServicePointController servicePointController) {
    this.repository = repository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    repository.save(ServicePointTestData.getBernWyleregg());

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

    mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.terminationInProgress", is(false)));
  }

  @Test
  void shouldNotStopServicePointTerminationWhenIdNotFound() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    long id = 456L;
    String sloid = servicePointVersionModel.getSloid();

    mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldNotStopServicePointTerminationWhenSloidDoesNotExists() throws Exception {
    long id = 123L;
    String sloid = "ch:1:sloid:753126";

    mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id))
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

    mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id)
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
    mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id)
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

    mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldTerminateServicePointAndStopTermination() throws Exception {
    ServicePointVersion servicePointVersion = ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
    servicePointVersion.setDesignationOfficial("Bern, Salem");
    servicePointVersion.setValidTo(LocalDate.of(2099, 12, 31));
    servicePointVersion.setStatus(Status.VALIDATED);
    servicePointVersion.setTerminationInProgress(true);

    ServicePointVersion version = repository.save(servicePointVersion);

    mvc.perform(post(
            "/internal/service-points/termination/terminate/" + version.getSloid() + "/" + version.getId() + "/" + LocalDate.of(2030,
                12, 31)))
        .andExpect(status().isOk());

    List<ServicePointVersion> result = repository.findAllByNumberOrderByValidFrom(version.getNumber());
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getValidTo()).isEqualTo(LocalDate.of(2030, 12, 31));
    assertThat(result.getFirst().isTerminationInProgress()).isFalse();
  }

  @Test
  void shouldChangeToTariffStop() throws Exception {
    ServicePointVersion servicePointVersion = ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
    servicePointVersion.setDesignationOfficial("Bern, Salem");
    servicePointVersion.setValidTo(LocalDate.of(2099, 12, 31));
    servicePointVersion.setStatus(Status.VALIDATED);
    servicePointVersion.setTerminationInProgress(true);
    assertThat(servicePointVersion.isStopPoint()).isTrue();
    assertThat(servicePointVersion.getOperatingPointTrafficPointType()).isNull();

    ServicePointVersion version = repository.save(servicePointVersion);
    Long id = version.getId();

    mvc.perform(post(
            "/internal/service-points/termination/change-to-tariff-stop/" + version.getSloid() + "/" + id + "/" + LocalDate.of(2030,
                12, 31)))
        .andExpect(status().isOk());

    List<ServicePointVersion> result = repository.findAllByNumberOrderByValidFrom(version.getNumber());
    assertThat(result).hasSize(2);
    assertThat(result.getFirst().getValidTo()).isEqualTo(LocalDate.of(2030, 12, 30));
    assertThat(result.getFirst().isStopPoint()).isTrue();

    assertThat(result.getLast().getOperatingPointTrafficPointType()).isEqualTo(OperatingPointTrafficPointType.TARIFF_POINT);
    assertThat(result.getLast().isStopPoint()).isFalse();
    assertThat(result.getLast().getStopPointType()).isNull();
    assertThat(result.getLast().hasGeolocation()).isFalse();
    assertThat(result.getLast().getBusinessOrganisation()).isEqualTo(ServicePointConstants.ALLIANCE_SWISS_PASS_SBOID);
    assertThat(result.getLast().getValidFrom()).isEqualTo(LocalDate.of(2030, 12, 31));
    assertThat(result.getLast().getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
  }

}
