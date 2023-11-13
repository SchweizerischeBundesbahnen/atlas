package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
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

 @Test
 void shouldThrowExceptionWhenSLOIDIsNotValidForPlatform() {
  String sloid = "ch:1:sloid:72839:0:100007:431234";
  assertThrows(SloidsNotEqualException.class,
      () -> crossValidationService.validateManuallyEnteredSloid(sloid, TrafficPointElementType.BOARDING_PLATFORM));
 }
 @Test
 void shouldNotThrowExceptionWhenSLOIDIsValidForPlatform() {
  String sloid = "ch:1:sloid:72839:0:100007";
  assertDoesNotThrow(() -> crossValidationService.validateManuallyEnteredSloid(sloid, TrafficPointElementType.BOARDING_PLATFORM));
 }
 @Test
 void shouldThrowExceptionWhenSLOIDIsNotValidForArea() {
  String sloid = "ch:1:sloid:72839:0:100007";

  assertThrows(SloidsNotEqualException.class,
      () -> crossValidationService.validateManuallyEnteredSloid(sloid, TrafficPointElementType.BOARDING_AREA));
 }

 @Test
 void shouldNotThrowExceptionWhenSLOIDIsValidForArea() {
  String sloid = "ch:1:sloid:72839:123";
  assertDoesNotThrow(() -> crossValidationService.validateManuallyEnteredSloid(sloid, TrafficPointElementType.BOARDING_AREA));
 }

}
