package ch.sbb.atlas.servicepointdirectory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrossValidationServiceTest {

  @Mock
  private ServicePointService servicePointService;

  private CrossValidationService crossValidationService;

  @BeforeEach
  void setUp() {
    crossValidationService = new CrossValidationService(servicePointService);
  }

  @Test
  void shouldThrowExceptionWhenValidateServicePointNumberExists() {
    when(servicePointService.isServicePointNumberExisting(any())).thenReturn(false);

    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
    assertThrows(ServicePointNumberNotFoundException.class,
        () -> crossValidationService.validateServicePointNumberExists(servicePointNumber));
  }

  @Test
  void shouldValidateServicePointNumberWhenValidateServicePointNumberExists() {
    when(servicePointService.isServicePointNumberExisting(any())).thenReturn(true);

    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
    assertDoesNotThrow(() -> crossValidationService.validateServicePointNumberExists(servicePointNumber));
  }

  @Test
  void shouldDetectMeanOfTransportTrainInAnyVersion() {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
    when(servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber)).thenReturn(
        List.of(servicePointVersion(Set.of(MeanOfTransport.TRAIN)), servicePointVersion(Set.of(MeanOfTransport.BUS))));

    assertTrue(crossValidationService.hasAnyVersionMeanOfTransportTrain(servicePointNumber));
  }

  @Test
  void shouldNotDetectMeanOfTransportTrainWhenNoVersionHasTrain() {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
    when(servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber)).thenReturn(
        List.of(servicePointVersion(Set.of(MeanOfTransport.BUS)), servicePointVersion(null)));

    assertFalse(crossValidationService.hasAnyVersionMeanOfTransportTrain(servicePointNumber));
  }

  @Test
  void shouldNotDetectMeanOfTransportTrainWhenServicePointHasNoVersions() {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
    when(servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber)).thenReturn(List.of());

    assertFalse(crossValidationService.hasAnyVersionMeanOfTransportTrain(servicePointNumber));
  }

  private ServicePointVersion servicePointVersion(Set<MeanOfTransport> meansOfTransport) {
    return ServicePointVersion.builder().meansOfTransport(meansOfTransport).build();
  }
}
