package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
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
}
