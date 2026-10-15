package ch.sbb.exportservice.job.prm.ersad.accessibility;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.wheelchairaccessibility.calculator.WheelchairAccessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.service.AccessibilityRequestFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WheelchairBasedErsadAccessibilityDeriverTest {

  private static final LocalDate EXPORT_DATE = LocalDate.of(2026, 9, 30);
  private static final LocalDate MIN_DATE = LocalDate.of(2000, 1, 1);
  private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
  private static final String STOP_POINT_SLOID = "ch:1:sloid:8500100";
  private static final String PLATFORM_1_SLOID = "ch:1:sloid:8500100:0:1";
  private static final String PLATFORM_2_SLOID = "ch:1:sloid:8500100:0:2";

  private final WheelchairBasedErsadAccessibilityDeriver deriver = new WheelchairBasedErsadAccessibilityDeriver();

  static Stream<Arguments> stateMapping() {
    return Stream.of(
        Arguments.of(WheelchairAccessibilityState.AUTONOMY, NetexTriState.TRUE),
        Arguments.of(WheelchairAccessibilityState.RAMP_USE, NetexTriState.PARTIAL),
        Arguments.of(WheelchairAccessibilityState.PRE_REGISTRATION, NetexTriState.PARTIAL),
        Arguments.of(WheelchairAccessibilityState.SHUTTLE, NetexTriState.PARTIAL),
        Arguments.of(WheelchairAccessibilityState.NO_ACCESS, NetexTriState.FALSE),
        Arguments.of(WheelchairAccessibilityState.NO_INFO, NetexTriState.UNKNOWN));
  }

  @ParameterizedTest
  @MethodSource("stateMapping")
  void shouldMapWheelchairAccessibilityStateToNetexTriState(WheelchairAccessibilityState state, NetexTriState expected) {
    // Given
    // When
    NetexTriState result = WheelchairBasedErsadAccessibilityDeriver.toNetex(state);

    // Then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldCoverEveryWheelchairAccessibilityStateInMappingTest() {
    // Given
    List<Object> testedStates = stateMapping().map(arguments -> arguments.get()[0]).toList();

    // When
    // Then
    assertThat(testedStates).containsExactlyInAnyOrder((Object[]) WheelchairAccessibilityState.values());
  }

  @Test
  void shouldReturnTrueWhenAllPlatformsAutonomous() {
    // Given
    List<PlatformVersion> platforms = List.of(autonomyPlatform(PLATFORM_1_SLOID).build(),
        autonomyPlatform(PLATFORM_2_SLOID).build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.TRUE);
  }

  @Test
  void shouldReturnPartialWhenOnePlatformNeedsRamp() {
    // Given
    List<PlatformVersion> platforms = List.of(autonomyPlatform(PLATFORM_1_SLOID).build(),
        autonomyPlatform(PLATFORM_2_SLOID)
            .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE).build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.PARTIAL);
  }

  @Test
  void shouldReturnFalseWhenAnyPlatformHasNoAccess() {
    // Given
    List<PlatformVersion> platforms = List.of(
        autonomyPlatform(PLATFORM_1_SLOID).build(),
        autonomyPlatform(PLATFORM_2_SLOID).levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO).build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.FALSE);
  }

  @Test
  void shouldReturnPartialWhenAnyPlatformHasShuttleEvenIfOtherHasNoAccess() {
    // Given
    List<PlatformVersion> platforms = List.of(
        autonomyPlatform(PLATFORM_1_SLOID).levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO).build(),
        autonomyPlatform(PLATFORM_2_SLOID).shuttle(BooleanOptionalAttributeType.YES).build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.PARTIAL);
  }

  @Test
  void shouldReturnUnknownWhenAnyPlatformHasNoInfoEvenIfOtherHasNoAccess() {
    // Given
    List<PlatformVersion> platforms = List.of(
        autonomyPlatform(PLATFORM_1_SLOID).levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO).build(),
        autonomyPlatform(PLATFORM_2_SLOID).levelAccessWheelchair(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED)
            .build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.UNKNOWN);
  }

  @Test
  void shouldReturnUnknownWhenNoPlatforms() {
    // Given
    StopPointVersion stopPoint = trainStopPoint().build();

    // When
    NetexTriState result = deriver.derive(stopPoint, List.of(), List.of(), EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.UNKNOWN);
  }

  @Test
  void shouldIgnorePlatformVersionsNotValidOnExportDate() {
    // Given
    List<PlatformVersion> platforms = List.of(
        autonomyPlatform(PLATFORM_1_SLOID).build(),
        autonomyPlatform(PLATFORM_2_SLOID).levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
            .validTo(EXPORT_DATE.minusDays(1)).build(),
        autonomyPlatform(PLATFORM_2_SLOID).validFrom(EXPORT_DATE).build());

    // When
    NetexTriState result = deriver.derive(trainStopPoint().build(), platforms, stepFreeRelationsFor(platforms),
        EXPORT_DATE);

    // Then
    assertThat(result).isEqualTo(NetexTriState.TRUE);
  }

  @Test
  void shouldProduceSameStateAsWheelchairExportForSameInput() {
    // Given
    StopPointVersion stopPoint = trainStopPoint().build();
    List<PlatformVersion> platforms = List.of(autonomyPlatform(PLATFORM_1_SLOID).build(),
        autonomyPlatform(PLATFORM_2_SLOID)
            .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE).build());
    List<RelationVersion> relations = stepFreeRelationsFor(platforms);
    WheelchairAccessibilityState wheelchairExportState = WheelchairAccessibility.calculateStopPoint(
            AccessibilityRequestFactory.forStopPoint(List.of(stopPoint), platforms, relations),
            new AccessibilityFilter(EXPORT_DATE, 1))
        .getAccessibilityInfos().getFirst().getAccessibilityState();

    // When
    NetexTriState result = deriver.derive(stopPoint, platforms, relations, EXPORT_DATE);

    // Then
    assertThat(wheelchairExportState).isEqualTo(WheelchairAccessibilityState.RAMP_USE);
    assertThat(result).isEqualTo(WheelchairBasedErsadAccessibilityDeriver.toNetex(wheelchairExportState));
  }

  private static StopPointVersion.StopPointVersionBuilder<?, ?> trainStopPoint() {
    return StopPointVersion.builder()
        .sloid(STOP_POINT_SLOID)
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .alternativeTransport(StandardAttributeType.NO)
        .assistanceService(StandardAttributeType.NOT_APPLICABLE)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .validFrom(MIN_DATE)
        .validTo(MAX_DATE);
  }

  private static PlatformVersion.PlatformVersionBuilder<?, ?> autonomyPlatform(String sloid) {
    return PlatformVersion.builder()
        .sloid(sloid)
        .parentServicePointSloid(STOP_POINT_SLOID)
        .shuttle(BooleanOptionalAttributeType.NO)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES)
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .superelevation(20.0)
        .validFrom(MIN_DATE)
        .validTo(MAX_DATE);
  }

  private static List<RelationVersion> stepFreeRelationsFor(List<PlatformVersion> platforms) {
    return platforms.stream()
        .map(PlatformVersion::getSloid)
        .distinct()
        .<RelationVersion>map(sloid -> RelationVersion.builder()
            .sloid(sloid)
            .parentServicePointSloid(STOP_POINT_SLOID)
            .stepFreeAccess(StepFreeAccessAttributeType.YES)
            .validFrom(MIN_DATE)
            .validTo(MAX_DATE)
            .build())
        .toList();
  }
}
