package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.AfterEach;
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

}
