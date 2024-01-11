package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSearchVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.versioning.service.VersionableService;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class ServicePointServiceTest {

  private ServicePointService servicePointService;

  @Mock
  private ServicePointVersionRepository servicePointVersionRepositoryMock;

  @Mock
  private VersionableService versionableServiceMock;

  @Mock
  private ServicePointValidationService servicePointValidationService;

  @Mock
  private ServicePointTerminationService servicePointTerminationService;

  @Mock
  private ServicePointSearchVersionRepository servicePointSearchVersionRepository;

  @Mock
  private ServicePointStatusDecider servicePointStatusDecider;

  @Mock
  private LocationClient locationClient;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.openMocks(this);
    servicePointService = new ServicePointService(servicePointVersionRepositoryMock, versionableServiceMock,
        servicePointValidationService, servicePointSearchVersionRepository, servicePointTerminationService,
        servicePointStatusDecider, locationClient);
  }

  @Test
  void shouldFindServicePoint() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);

    // when
    servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);

    // then
    verify(servicePointVersionRepositoryMock).findAllByNumberOrderByValidFrom(eq(servicePointNumber));
  }

  @Test
  void shouldCallFindServicePointWithRouteNetworkTrue() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);

    // when
    servicePointService.findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(servicePointNumber);

    // then
    verify(servicePointVersionRepositoryMock).findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(
        eq(servicePointNumber));
  }

  @Test
  void shouldCallExistsByServicePointNumber() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);

    // when
    servicePointService.isServicePointNumberExisting(servicePointNumber);

    // then
    verify(servicePointVersionRepositoryMock).existsByNumber(eq(servicePointNumber));
  }

  @Test
  void shouldCallFindById() {
    // given & when
    servicePointService.findById(123L);

    // then
    verify(servicePointVersionRepositoryMock).findById(eq(123L));
  }

  @Test
  void shouldGetCurrentServicePointVersionWhenValidFromPerfectMatch() {
    //given
    ServicePointVersion version = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentServicePointVersionWhenValidToPerfectMatch() {
    //given
    ServicePointVersion version = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentServicePointVersionWhenEditedVersionIsBetweenDbVersion() {
    //given
    ServicePointVersion version = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGetCurrentServicePointVersionWhenFoundMultipleVersionWhenEditedVersionIsBetweenDbVersion() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  /**
   * given |------------|-----------|
   * edit                             |-----------|
   * return             |-----------|
   */
  @Test
  void shouldGetCurrentServicePointVersionWhenNoCurrentVersionMatchedAndReturnTheLastVersion() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 12, 31))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 6, 2));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 30));
  }

  /**
   * given               |------------|-----------|
   * edit  |-----------|
   * return             |------------|
   */
  @Test
  void shouldGetCurrentServicePointVersionWhenNoCurrentVersionMatchedAndReturnTheFirstVersion() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 1, 1))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 2));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 6, 1));
  }

  /**
   * given  |------------|-----------|
   * edit   |------------------------|
   * return |------------|-----------|
   */
  @Test
  void shouldReturnTheFirstVersionWhenTheEditVersionMatchExactlyMoreThenOneVersion() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    ServicePointVersion result = ImportUtils.getCurrentPointVersion(versions, edited);
    assertThat(result).isNotNull();
  }

  @Test
  void shouldUpdateServicePointVersion() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    ServicePointVersion edited = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 2))
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    //when
    ServicePointVersion result = servicePointService.updateServicePointVersion(version1, edited, Collections.emptyList());
    //then
    assertThat(result).isNotNull();
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 6, 1));

    verify(servicePointTerminationService).checkTerminationAllowed(anyList(), anyList());
  }

  @Test
  void shouldUpdateServicePointVersionAndThrowException() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 6, 1))
        .build();
    version1.setVersion(1);
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 6, 2))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    version2.setVersion(2);
    // when then
    Assertions.assertThrows(StaleObjectStateException.class,
        () -> servicePointService.updateServicePointVersion(version1, version2, Collections.emptyList()));
  }

}
