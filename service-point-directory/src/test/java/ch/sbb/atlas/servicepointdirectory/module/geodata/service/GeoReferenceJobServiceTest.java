package ch.sbb.atlas.servicepointdirectory.module.geodata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.geodata.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer.VersionDataRange;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class GeoReferenceJobServiceTest {

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private ServicePointGeoDataService servicePointGeoDataService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private ServicePointService servicePointService;

  @Autowired
  private GeoReferenceJobService geoReferenceJobService;

  @AfterEach
  public void cleanUp() {
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldNotUpdateGeoLocationWithOneVersion() {
    //given
    ServicePointVersion servicePointVersion = servicePointService.createAndPublish(ServicePointTestData.getBernWyleregg(),
        Optional.empty(), List.of());
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    when(servicePointGeoDataService.getGeoReferenceInformation(any(ServicePointGeolocation.class))).thenReturn(
        servicePointGeolocation);

    //when
    UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(servicePointVersion.getId());

    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldUpdateGeoLocationWithOneVersion() {
    //given
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    bernWyleregg.setValidTo(LocalDate.of(9999, 1, 31));
    ServicePointVersion servicePointVersion = servicePointService.createAndPublish(bernWyleregg,
        Optional.empty(), List.of());
    ServicePointGeolocation servicePointGeolocationWithUpdatedGeo = ServicePointGeolocation.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.VAUD)
        .swissDistrictNumber(2230)
        .swissDistrictName("Riviera-Pays-d'Enhaut")
        .swissMunicipalityNumber(5841)
        .swissMunicipalityName("Château-d'Oex")
        .swissLocalityName("La Lécherette")
        .height(1201.0)
        .east(123.2)
        .north(22132.12)
        .spatialReference(SpatialReference.LV95)
        .build();
    when(servicePointGeoDataService.getGeoReferenceInformation(any(ServicePointGeolocation.class))).thenReturn(
        servicePointGeolocationWithUpdatedGeo);

    //when
    UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(servicePointVersion.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getSloid()).isEqualTo(servicePointVersion.getSloid());
    assertThat(result.getId()).isEqualTo(servicePointVersion.getId());

    List<ServicePointVersion> versionsResult = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersion.getNumber());
    assertThat(versionsResult).hasSize(2);

    ServicePointGeolocation updatedServicePointGeolocationResult = result.getUpdatedServicePointGeolocation();
    assertThat(updatedServicePointGeolocationResult.getSwissCanton()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissCanton());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityName()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissMunicipalityName());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityNumber()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissMunicipalityNumber());
    assertThat(updatedServicePointGeolocationResult.getSwissLocalityName()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissLocalityName());

    ServicePointGeolocation currentServicePointGeolocationResult = result.getCurrentServicePointGeolocation();
    assertThat(currentServicePointGeolocationResult).usingRecursiveComparison()
        .isEqualTo(servicePointVersion.getServicePointGeolocation());

    assertThat(result.getCurrentVersionsDataRange()).hasSize(1);
    assertThat(result.getUpdatedVersionsDataRange()).hasSize(2);
    assertThat(result.getResponseMessage()).isNotNull();

    assertThat(result.getCurrentVersionsDataRange()).hasSize(1).containsExactlyInAnyOrder(
        new VersionDataRange(bernWyleregg.getValidFrom(), bernWyleregg.getValidTo()));
    assertThat(result.getUpdatedVersionsDataRange()).hasSize(2).containsExactly(
        new VersionDataRange(versionsResult.getFirst().getValidFrom(), versionsResult.getFirst().getValidTo()),
        new VersionDataRange(versionsResult.getLast().getValidFrom(), versionsResult.getLast().getValidTo()));
    assertThat(result.getResponseMessage()).isNotNull();
  }

  @Test
  void shouldUpdateGeoLocationWithMerge() {
    //given
    ServicePointVersion version = ServicePointTestData.getBernWyleregg();
    version.setValidTo(LocalDate.of(9999, 1, 31));
    ServicePointVersion servicePointVersion = servicePointService.createAndPublish(version, Optional.empty(), List.of());
    ServicePointVersion servicePointVersionEdited = servicePointService.getServicePointVersionById(servicePointVersion.getId());
    servicePointVersionEdited.getServicePointGeolocation().setSwissDistrictName("Changed");
    LocalDate newValidFrom = version.getValidTo().plusDays(1);
    servicePointVersionEdited.setValidFrom(newValidFrom);
    LocalDate newValidTo = version.getValidTo().plusDays(31);
    servicePointVersionEdited.setValidTo(newValidTo);
    servicePointService.updateServicePointVersion(version, servicePointVersionEdited,
        servicePointService.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber()));
    ServicePointGeolocation servicePointGeolocationWithUpdatedGeo = ServicePointGeolocation.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(246)
        .swissDistrictName("Bern-Mittelland")
        .swissMunicipalityNumber(351)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .height(555D)
        .east(2600783D)
        .north(1201099D)
        .spatialReference(SpatialReference.LV95)
        .build();
    when(servicePointGeoDataService.getGeoReferenceInformation(any(ServicePointGeolocation.class))).thenReturn(
        servicePointGeolocationWithUpdatedGeo);
    ServicePointVersion versionToUpdate = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersion.getNumber()).getLast();

    //when
    UpdateGeoLocationResultContainer result =
        geoReferenceJobService.updateGeoLocation(versionToUpdate.getId());

    //then
    List<ServicePointVersion> versionsResult = servicePointService.findAllByNumberOrderByValidFrom(
        versionToUpdate.getNumber());
    assertThat(versionsResult).hasSize(1);
    assertThat(versionsResult.getFirst().getId()).isEqualTo(versionToUpdate.getId());
    ServicePointVersion updatedVersion = versionsResult.getFirst();
    assertThat(result).isNotNull();
    assertThat(result.getSloid()).isEqualTo(versionToUpdate.getSloid());
    assertThat(result.getId()).isEqualTo(versionToUpdate.getId());

    ServicePointGeolocation currentServicePointGeolocationResult = result.getCurrentServicePointGeolocation();
    assertThat(currentServicePointGeolocationResult).usingRecursiveComparison()
        .isEqualTo(versionToUpdate.getServicePointGeolocation());

    ServicePointGeolocation updatedServicePointGeolocationResult = result.getUpdatedServicePointGeolocation();
    assertThat(updatedServicePointGeolocationResult.getSwissCanton()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissCanton());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityName()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissMunicipalityName());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityNumber()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissMunicipalityNumber());
    assertThat(updatedServicePointGeolocationResult.getSwissLocalityName()).isEqualTo(
        servicePointGeolocationWithUpdatedGeo.getSwissLocalityName());

    assertThat(result.getCurrentVersionsDataRange()).hasSize(2).containsExactlyInAnyOrder(
        new VersionDataRange(servicePointVersion.getValidFrom(), servicePointVersion.getValidTo()),
        new VersionDataRange(newValidFrom, newValidTo));
    assertThat(result.getUpdatedVersionsDataRange()).hasSize(1).containsExactly(
        new VersionDataRange(updatedVersion.getValidFrom(), updatedVersion.getValidTo()));
    assertThat(result.getResponseMessage()).isNotNull();
  }
}