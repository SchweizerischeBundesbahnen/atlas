package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServicePointServiceTest {

  private ServicePointService servicePointService;

  @Mock
  private ServicePointVersionRepository servicePointVersionRepositoryMock;

  @Mock
  private VersionableService versionableServiceMock;

  private AutoCloseable mocks;

  @BeforeEach
  void initMocksAndService() {
    mocks = MockitoAnnotations.openMocks(this);
    servicePointService = new ServicePointService(servicePointVersionRepositoryMock, versionableServiceMock);
  }

  @AfterEach
  void closeMocks() throws Exception {
    mocks.close();
  }

  @Test
  void shouldFindServicePoint() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.of(123);

    // when
    servicePointService.findServicePoint(servicePointNumber);

    // then
    verify(servicePointVersionRepositoryMock).findAllByNumberOrderByValidFrom(eq(servicePointNumber));
  }

  @Test
  void shouldCallExistsByServicePointNumber() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.of(123);

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
  void shouldCallDeleteById() {
    // given & when
    servicePointService.deleteById(123L);

    // then
    verify(servicePointVersionRepositoryMock).deleteById(eq(123L));
  }

  @Test
  void shouldCallSave() {
    // given
    ServicePointVersion servicePointVersion = ServicePointTestData.getBernWyleregg();

    // when
    servicePointService.save(servicePointVersion);

    // then
    verify(servicePointVersionRepositoryMock).save(eq(servicePointVersion));
  }

  @Test
  public void shouldGetCurrentServicePointVersionWhenValidFromPerfectMatch() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  public void shouldGetCurrentServicePointVersionWhenValidToPerfectMatch() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  public void shouldGetCurrentServicePointVersionWhenEditedVersionIsBetweenDbVersion() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  public void shouldGetCurrentServicePointVersionWhenFoundMultipleVersionWhenEditedVersionIsBetweenDbVersion() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
  }

  /**
   * given |------------|-----------|
   * edit                             |-----------|
   * return             |-----------|
   */
  @Test
  public void shouldGetCurrentServicePointVersionWhenNoCurrentversionMatchedAndReturnTheLastVersion() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
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
  public void shouldGetCurrentServicePointVersionWhenNoCurrentversionMatchedAndReturnTheFirstVersion() {
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
    ServicePointVersion result = servicePointService.getCurrentServicePointVersion(versions, edited);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 2));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2000, 6, 1));
  }

  @Test
  public void shouldThrowExceptionWhenNoMatchWasFound() {
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
        .validTo(LocalDate.of(2000, 12, 30))
        .build();
    List<ServicePointVersion> versions = new ArrayList<>();
    versions.add(version1);
    versions.add(version2);
    //when
    Assertions.assertThrows(RuntimeException.class, () -> {
      servicePointService.getCurrentServicePointVersion(versions, edited);
    });
  }

}
