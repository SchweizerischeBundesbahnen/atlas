package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrafficPointElementValidationServiceTest {

    @Mock
    private ServicePointService servicePointService;

    private TrafficPointElementValidationService trafficPointElementValidationService;

    @BeforeEach
    public void setUp() {
        trafficPointElementValidationService = new TrafficPointElementValidationService(servicePointService);
    }

    @Test
    public void shouldThrowExceptionWhenValidateServicePointNumberExists() {
        when(servicePointService.isServicePointNumberExisting(any())).thenReturn(false);

        ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
        assertThrows(ServicePointNumberNotFoundException.class,
                () -> trafficPointElementValidationService.validateServicePointNumberExists(servicePointNumber));
    }

    @Test
    public void shouldValidateServicePointNumberWhenValidateServicePointNumberExists() {
        when(servicePointService.isServicePointNumberExisting(any())).thenReturn(true);

        ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(1234567);
        assertDoesNotThrow(() -> trafficPointElementValidationService.validateServicePointNumberExists(servicePointNumber));
    }

}
