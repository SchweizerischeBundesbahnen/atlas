package ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.exception.DesignationOperationalMaxDigitsException;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrafficPointElementValidationServiceTest {

  private static final ServicePointNumber SERVICE_POINT_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(8589108);

  @Mock
  private CrossValidationService crossValidationService;

  private TrafficPointElementValidationService trafficPointElementValidationService;

  @BeforeEach
  void setUp() {
    trafficPointElementValidationService = new TrafficPointElementValidationService(crossValidationService);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "42", "999"})
  void shouldAllowUpToThreeDigitsWhenServicePointHasMeanOfTransportTrain(String designationOperational) {
    when(crossValidationService.hasAnyVersionMeanOfTransportTrain(SERVICE_POINT_NUMBER)).thenReturn(true);

    assertThatNoException().isThrownBy(() -> trafficPointElementValidationService.validateDesignationOperational(
        trafficPointElementVersion(designationOperational)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "99"})
  void shouldAllowUpToTwoDigitsWhenServicePointHasNoMeanOfTransportTrain(String designationOperational) {
    when(crossValidationService.hasAnyVersionMeanOfTransportTrain(SERVICE_POINT_NUMBER)).thenReturn(false);

    assertThatNoException().isThrownBy(() -> trafficPointElementValidationService.validateDesignationOperational(
        trafficPointElementVersion(designationOperational)));
  }

  @Test
  void shouldThrowExceptionWhenThreeDigitsAndServicePointHasNoMeanOfTransportTrain() {
    when(crossValidationService.hasAnyVersionMeanOfTransportTrain(SERVICE_POINT_NUMBER)).thenReturn(false);

    assertThatExceptionOfType(DesignationOperationalMaxDigitsException.class)
        .isThrownBy(() -> trafficPointElementValidationService.validateDesignationOperational(
            trafficPointElementVersion("123")))
        .satisfies(exception -> {
          assertThat(exception.getErrorResponse().getStatus()).isEqualTo(400);
          assertThat(exception.getErrorResponse().getDetails().first().getField()).isEqualTo("designationOperational");
          assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode())
              .isEqualTo(DesignationOperationalMaxDigitsException.ERROR_CODE);
        });
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  void shouldSkipValidationWhenDesignationOperationalIsBlank(String designationOperational) {
    assertThatNoException().isThrownBy(() -> trafficPointElementValidationService.validateDesignationOperational(
        trafficPointElementVersion(designationOperational)));

    verifyNoMoreInteractions(crossValidationService);
  }

  @Test
  void shouldSkipValidationWhenDesignationOperationalIsNull() {
    assertThatNoException().isThrownBy(() -> trafficPointElementValidationService.validateDesignationOperational(
        trafficPointElementVersion(null)));

    verifyNoMoreInteractions(crossValidationService);
  }

  private TrafficPointElementVersion trafficPointElementVersion(String designationOperational) {
    return TrafficPointElementVersion.builder()
        .servicePointNumber(SERVICE_POINT_NUMBER)
        .designationOperational(designationOperational)
        .build();
  }

}
