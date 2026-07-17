package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.location.LocationGeoClient;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.module.geodata.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Reproduction test for the reported bug: updating a StopPoint and shortening its validTo (which splits the timeline
 * into two versions) must not drop meansOfTransport / stopPointType from either resulting version, even though the
 * request body omits those fields.
 */
class ServicePointUpdateStopPointDataApiTest extends BaseControllerApiTest {

  private static final int NUMBER_WITHOUT_CHECK_DIGIT = 8500004;

  @MockitoBean
  private LocationGeoClient locationGeoClient;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  private final ServicePointVersionRepository repository;

  @Autowired
  ServicePointUpdateStopPointDataApiTest(ServicePointVersionRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void setUpMocks() {
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:4");
    when(locationGeoClient.getLocationInformation(any(), anyBoolean())).thenReturn(
        GeoReference.builder()
            .country(Country.SWITZERLAND)
            .swissCanton(SwissCanton.SOLOTHURN)
            .swissDistrictNumber(1107)
            .swissDistrictName("Lebern")
            .swissMunicipalityNumber(2546)
            .swissMunicipalityName("Grenchen")
            .swissLocalityName("Grenchen")
            .height(458.3)
            .build());
  }

  @AfterEach
  void cleanUp() {
    repository.deleteAll();
  }

  @Test
  void shouldKeepMeansOfTransportAndStopPointTypeWhenShorteningValidToSplitsTheVersion() throws Exception {
    ServicePointVersion saved = repository.saveAndFlush(buildStopPointVersion());

    UpdateServicePointVersionModel update = UpdateServicePointVersionModel.builder()
        .designationOfficial("HelloWorld2")
        .businessOrganisation("ch:1:sboid:1100001")
        .categories(List.of())
        // meansOfTransport and stopPointType intentionally omitted (as in the reported request)
        .validFrom(LocalDate.of(2026, 7, 1))
        .validTo(LocalDate.of(6000, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationMapper.toCreateModel(buildGeolocation()))
        .etagVersion(saved.getVersion())
        .build();

    mvc.perform(put("/v1/service-points/" + saved.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        // edited version: request cleared stopPointType / meansOfTransport -> now a plain service point
        .andExpect(jsonPath("$[0].validFrom", is("2026-07-01")))
        .andExpect(jsonPath("$[0].validTo", is("6000-12-31")))
        .andExpect(jsonPath("$[0].meansOfTransport", hasSize(0)))
        .andExpect(jsonPath("$[0].stopPointType", is(nullValue())))
        .andExpect(jsonPath("$[0].stopPoint", is(false)))
        // split remainder version: must keep the original stop point data
        .andExpect(jsonPath("$[1].validFrom", is("6001-01-01")))
        .andExpect(jsonPath("$[1].validTo", is("9999-12-31")))
        .andExpect(jsonPath("$[1].meansOfTransport", contains("TRAIN")))
        .andExpect(jsonPath("$[1].stopPointType", is("ORDERLY")))
        .andExpect(jsonPath("$[1].stopPoint", is(true)));

    List<ServicePointVersion> persisted = repository.findAllByNumberOrderByValidFrom(
        ServicePointNumber.ofNumberWithoutCheckDigit(NUMBER_WITHOUT_CHECK_DIGIT));
    assertThat(persisted).hasSize(2);

    ServicePointVersion editedVersion = persisted.get(0);
    assertThat(editedVersion.getValidTo()).isEqualTo(LocalDate.of(6000, 12, 31));
    assertThat(editedVersion.getMeansOfTransport()).isEmpty();
    assertThat(editedVersion.getStopPointType()).isNull();

    ServicePointVersion remainderVersion = persisted.get(1);
    assertThat(remainderVersion.getValidFrom()).isEqualTo(LocalDate.of(6001, 1, 1));
    assertThat(remainderVersion.getMeansOfTransport()).containsExactly(MeanOfTransport.TRAIN);
    assertThat(remainderVersion.getStopPointType()).isEqualTo(StopPointType.ORDERLY);
  }

  private ServicePointVersion buildStopPointVersion() {
    ServicePointGeolocation geolocation = buildGeolocation();
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .servicePointGeolocation(geolocation)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(NUMBER_WITHOUT_CHECK_DIGIT))
        .sloid("ch:1:sloid:4")
        .numberShort(4)
        .country(Country.SWITZERLAND)
        .designationOfficial("HelloWorld2")
        .businessOrganisation("ch:1:sboid:1100001")
        .status(Status.DRAFT)
        .validFrom(LocalDate.of(2026, 7, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .categories(new java.util.HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ORDERLY)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .creator("u250372")
        .editor("u250372")
        .build();
    geolocation.setServicePointVersion(servicePoint);
    return servicePoint;
  }

  private ServicePointGeolocation buildGeolocation() {
    return ServicePointGeolocation.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2597573.69103)
        .north(1227143.7969)
        .height(458.3)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.SOLOTHURN)
        .swissDistrictNumber(1107)
        .swissDistrictName("Lebern")
        .swissMunicipalityNumber(2546)
        .swissMunicipalityName("Grenchen")
        .swissLocalityName("Grenchen")
        .build();
  }
}


