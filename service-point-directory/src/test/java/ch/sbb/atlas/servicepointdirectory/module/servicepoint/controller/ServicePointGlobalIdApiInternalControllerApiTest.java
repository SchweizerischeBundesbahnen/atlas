package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.location.LocationGeoClient;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid.GlobalIdUpdateModel;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointGlobalIdRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ServicePointGlobalIdApiInternalControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private LocationGeoClient locationGeoClient;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  private final ServicePointVersionRepository repository;
  private final ServicePointGlobalIdRepository servicePointGlobalIdRepository;
  private final ServicePointApiV1Controller servicePointController;

  private ServicePointNumber germanNumber;

  @Autowired
  ServicePointGlobalIdApiInternalControllerApiTest(ServicePointVersionRepository repository,
      ServicePointGlobalIdRepository servicePointGlobalIdRepository,
      ServicePointApiV1Controller servicePointController) {
    this.repository = repository;
    this.servicePointGlobalIdRepository = servicePointGlobalIdRepository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createGermanServicePoint() {
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:1");
    when(locationGeoClient.getLocationInformation(any(), anyBoolean())).thenReturn(
        GeoReference.builder()
            .country(Country.SWITZERLAND)
            .swissCanton(SwissCanton.BERN)
            .swissDistrictNumber(246)
            .swissDistrictName("Bern-Mittelland")
            .swissMunicipalityNumber(351)
            .swissMunicipalityName("Bern")
            .swissLocalityName("Bern")
            .height(555D)
            .build());

    CreateServicePointVersionModel germanServicePoint = ServicePointTestData.getAargauServicePointVersionModel();
    germanServicePoint.setCountry(Country.GERMANY);
    germanServicePoint.setNumberShort(12345);
    servicePointController.createServicePoint(germanServicePoint);
    germanNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8012345);
  }

  @AfterEach
  void cleanUpDb() {
    servicePointGlobalIdRepository.deleteAll();
    repository.deleteAll();
  }

  private String body(String globalId) throws Exception {
    return mapper.writeValueAsString(GlobalIdUpdateModel.builder().globalId(globalId).build());
  }

  @Test
  void shouldStoreGlobalIdForGermanServicePoint() throws Exception {
    // When / Then
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body("de:05770:1282")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ReadServicePointVersionModel.Fields.globalId, is("de:05770:1282")));

    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(germanNumber)).isPresent();
  }

  @Test
  void shouldUpdateExistingGlobalId() throws Exception {
    // Given
    servicePointGlobalIdRepository.save(ServicePointGlobalId.builder()
        .servicePointNumber(germanNumber)
        .globalId("de:00000:1")
        .build());

    // When / Then
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body("de:05770:1282")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ReadServicePointVersionModel.Fields.globalId, is("de:05770:1282")));

    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(germanNumber))
        .get().extracting(ServicePointGlobalId::getGlobalId).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldRemoveGlobalIdViaDelete() throws Exception {
    // Given
    servicePointGlobalIdRepository.save(ServicePointGlobalId.builder()
        .servicePointNumber(germanNumber)
        .globalId("de:05770:1282")
        .build());

    // When / Then
    mvc.perform(delete("/internal/service-points/8012345/global-id")
            .contentType(contentType))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ReadServicePointVersionModel.Fields.globalId, is(nullValue())));

    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(germanNumber)).isEmpty();
  }

  @Test
  void shouldReturnNotFoundWhenDeletingGlobalIdForUnknownServicePointNumber() throws Exception {
    // When / Then
    mvc.perform(delete("/internal/service-points/8077777/global-id")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldRejectNullGlobalIdOnUpdate() throws Exception {
    // When / Then
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body(null)))
        .andExpect(status().isBadRequest());

    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(germanNumber)).isEmpty();
  }

  @Test
  void shouldRejectGermanServicePointWithAustrianGlobalId() throws Exception {
    // When / Then
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body("at:42:9379")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.COUNTRY_MISMATCH")));

    assertThat(servicePointGlobalIdRepository.findByGlobalId("at:42:9379")).isEmpty();
  }

  @Test
  void shouldTrimLeadingAndTrailingWhitespace() throws Exception {
    // When - the value is sent with surrounding whitespace (globally trimmed on deserialization)
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body(" de:05770:1282 ")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ReadServicePointVersionModel.Fields.globalId, is("de:05770:1282")));

    // Then - it is stored trimmed
    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(germanNumber))
        .get().extracting(ServicePointGlobalId::getGlobalId).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldRejectGlobalIdAlreadyUsedByAnotherStop() throws Exception {
    // Given - the Global-ID is already assigned to another stop
    servicePointGlobalIdRepository.save(ServicePointGlobalId.builder()
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8099999))
        .globalId("de:05770:1282")
        .build());

    // When / Then
    mvc.perform(put("/internal/service-points/8012345/global-id")
            .contentType(contentType)
            .content(body("de:05770:1282")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ALREADY_USED")));
  }

  @Test
  void shouldRejectSwissServicePointWithGlobalId() throws Exception {
    // Given - a Swiss service point
    repository.save(ServicePointTestData.getBernWyleregg());

    // When / Then
    mvc.perform(put("/internal/service-points/8589008/global-id")
            .contentType(contentType)
            .content(body("de:05770:1282")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details[0].displayInfo.code",
            is("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.NOT_ALLOWED_FOR_COUNTRY")));
  }

  @Test
  void shouldReturnNotFoundForUnknownServicePointNumber() throws Exception {
    // When / Then
    mvc.perform(put("/internal/service-points/8077777/global-id")
            .contentType(contentType)
            .content(body("de:05770:1282")))
        .andExpect(status().isNotFound());
  }

}
