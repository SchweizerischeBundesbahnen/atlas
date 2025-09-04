package ch.sbb.atlas.servicepointdirectory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepointdirectory.module.sector.exception.MissingTrainStopPointException;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorValidityException;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service.TrafficPointElementService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedSectorServiceTest {

  private SharedSectorService sharedSectorService;

  @Mock
  private TrafficPointElementService trafficPointElementService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedSectorService = new SharedSectorService(trafficPointElementService);
  }

  @Test
  void shouldValidateMeanOfTransportOfServicePoint() {
    List<ServicePointVersion> servicePointVersions = List.of(ServicePointTestData.getBern(),
        ServicePointTestData.getBernWyleregg());

    assertDoesNotThrow(() -> sharedSectorService.validateMeanOfTransportOfServicePoint(servicePointVersions));
  }

  @Test
  void shouldThrowWhenMeanOfTransportOfServicePointIsNotTrain() {
    List<ServicePointVersion> servicePointVersions = List.of(ServicePointTestData.getBernOst());

    assertThrows(MissingTrainStopPointException.class,
        () -> sharedSectorService.validateMeanOfTransportOfServicePoint(servicePointVersions));
  }

  @Test
  void shouldValidateValidityOfSector() {
    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("ch:1:sgid:123456789")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 1, 1))
        .designation("test")
        .build();

    when(trafficPointElementService.findBySloidOrderByValidFrom(any())).thenReturn(
        List.of(TrafficPointTestData.getBasicTrafficPoint()));

    assertDoesNotThrow(() -> sharedSectorService.validateValidity(sectorGroupVersion));
  }

  @Test
  void shouldThrowWhenValidityOfSectorIsNotMatchingValidityOfTrafficPoint() {
    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("ch:1:sgid:123456789")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2003, 1, 1))
        .designation("test")
        .build();

    when(trafficPointElementService.findBySloidOrderByValidFrom(any())).thenReturn(
        List.of(TrafficPointTestData.getBasicTrafficPoint()));

    assertThrows(SectorValidityException.class, () -> sharedSectorService.validateValidity(sectorGroupVersion));
  }
}
