package ch.sbb.atlas.servicepointdirectory.service.georeference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer.VersionDataRange;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class GeoReferenceJobServiceTest {

  @MockBean
  private GeoReferenceService geoReferenceService;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

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
    ServicePointVersion servicePointVersion = servicePointService.create(ServicePointTestData.getBernWyleregg(),
        Optional.empty(), List.of());
    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(246)
        .swissDistrictName("Bern-Mittelland")
        .swissMunicipalityNumber(351)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .height(555D)
        .build();
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    when(geoReferenceService.getGeoReference(any(), eq(servicePointGeolocation.getHeight() == null)))
        .thenReturn(geoReference);
    //when
    UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(
        servicePointVersion.getId());

    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldUpdateGeoLocationWithOneVersion() {
    //given
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    bernWyleregg.setValidTo(LocalDate.of(9999, 1, 31));
    ServicePointVersion servicePointVersion = servicePointService.create(bernWyleregg,
        Optional.empty(), List.of());
    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.VAUD)
        .swissDistrictNumber(2230)
        .swissDistrictName("Riviera-Pays-d'Enhaut")
        .swissMunicipalityNumber(5841)
        .swissMunicipalityName("Château-d'Oex")
        .swissLocalityName("La Lécherette")
        .height(1201.0)
        .build();
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    when(geoReferenceService.getGeoReference(any(), eq(servicePointGeolocation.getHeight() == null)))
        .thenReturn(geoReference);
    //when
    UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(
        servicePointVersion.getId());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getSloid()).isEqualTo(servicePointVersion.getSloid());
    assertThat(result.getId()).isEqualTo(servicePointVersion.getId());

    List<ServicePointVersion> versionsResult = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersion.getNumber());
    assertThat(versionsResult).hasSize(2);

    ServicePointGeolocation updatedServicePointGeolocationResult = result.getUpdatedServicePointGeolocation();
    assertThat(updatedServicePointGeolocationResult.getSwissCanton()).isEqualTo(geoReference.getSwissCanton());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityName()).isEqualTo(
        geoReference.getSwissMunicipalityName());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityNumber()).isEqualTo(
        geoReference.getSwissMunicipalityNumber());
    assertThat(updatedServicePointGeolocationResult.getSwissLocalityName()).isEqualTo(geoReference.getSwissLocalityName());

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
    ServicePointVersion servicePointVersion = servicePointService.create(version, Optional.empty(), List.of());
    ServicePointVersion servicePointVersionEdited = servicePointService.getServicePointVersionById(servicePointVersion.getId());
    servicePointVersionEdited.getServicePointGeolocation().setSwissDistrictName("Changed");
    LocalDate newValidFrom = version.getValidTo().plusDays(1);
    servicePointVersionEdited.setValidFrom(newValidFrom);
    LocalDate newValidTo = version.getValidTo().plusDays(31);
    servicePointVersionEdited.setValidTo(newValidTo);
    servicePointService.updateServicePointVersion(version, servicePointVersionEdited,
        servicePointService.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber()));
    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(246)
        .swissDistrictName("Bern-Mittelland")
        .swissMunicipalityNumber(351)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .height(555D)
        .build();
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    when(geoReferenceService.getGeoReference(any(), eq(servicePointGeolocation.getHeight() == null)))
        .thenReturn(geoReference);
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
    assertThat(updatedServicePointGeolocationResult.getSwissCanton()).isEqualTo(geoReference.getSwissCanton());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityName()).isEqualTo(
        geoReference.getSwissMunicipalityName());
    assertThat(updatedServicePointGeolocationResult.getSwissMunicipalityNumber()).isEqualTo(
        geoReference.getSwissMunicipalityNumber());
    assertThat(updatedServicePointGeolocationResult.getSwissLocalityName()).isEqualTo(geoReference.getSwissLocalityName());

    assertThat(result.getCurrentVersionsDataRange()).hasSize(2).containsExactlyInAnyOrder(
        new VersionDataRange(servicePointVersion.getValidFrom(), servicePointVersion.getValidTo()),
        new VersionDataRange(newValidFrom, newValidTo));
    assertThat(result.getUpdatedVersionsDataRange()).hasSize(1).containsExactly(
        new VersionDataRange(updatedVersion.getValidFrom(), updatedVersion.getValidTo()));
    assertThat(result.getResponseMessage()).isNotNull();
  }

}