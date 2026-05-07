package ch.sbb.prm.directory.module.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class StopPointCompleteAccessibilityCalculatorTest {

  private final StopPointCompleteAccessibilityCalculator calculator = new StopPointCompleteAccessibilityCalculator();

  @ParameterizedTest
  @EnumSource(value = StandardAttributeType.class, names = {"YES", "PARTIALLY"})
  void shouldReturnShuttleWhenAlternativeTransportIsYesOrPartially(StandardAttributeType alternativeTransport) {
    StopPointVersion stopPoint = StopPointVersion.builder()
        .alternativeTransport(alternativeTransport)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @ParameterizedTest
  @EnumSource(value = StandardAttributeType.class, names = {"YES", "PARTIALLY"})
  void shouldReturnPreRegistrationWhenAssistanceServiceAndAvailabilityAreYesOrPartially(StandardAttributeType availability) {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.YES)
        .assistanceAvailability(availability)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnRampUseWhenAssistanceServiceIsYesAndAvailabilityIsNo() {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.YES)
        .assistanceAvailability(StandardAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.RAMP_USE);
  }

  @Test
  void shouldReturnPreRegistrationWhenRequestFulfilledYesAndServiceNotApplicable() {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.YES)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.PRE_REGISTRATION);
  }

  @Test
  void shouldReturnNoAccessWhenAssistanceRequestFulfilledIsNo() {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.NO)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoAccessWhenAssistanceServiceIsNo() {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.NO)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_ACCESS);
  }

  @Test
  void shouldReturnNoInfoWhenNoConditionMatches() {
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  @ParameterizedTest
  @EnumSource(value = StandardAttributeType.class, mode = Mode.EXCLUDE, names = {"YES", "PARTIALLY"})
  void shouldFallThroughWhenAssistanceServiceIsYesButAvailabilityIsNeitherYesPartiallyNorNo(StandardAttributeType availability) {
    if (availability == StandardAttributeType.NO) {
      return;
    }
    StopPointVersion stopPoint = stopPointBuilder()
        .assistanceService(StandardAttributeType.YES)
        .assistanceAvailability(availability)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .build();

    WheelchairAccessibilityState result = calculator.calculateStopPoint(stopPoint);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.NO_INFO);
  }

  private static StopPointVersion.StopPointVersionBuilder<?, ?> stopPointBuilder() {
    return StopPointVersion.builder()
        .alternativeTransport(StandardAttributeType.NO);
  }
}
