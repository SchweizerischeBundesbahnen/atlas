package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatformTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelationTestData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRepresentation;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPointTestData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StopPointWheelchairAccessibilityScenarioTest {

  @Test
  @DisplayName("Testcase 1: Solothurn mit 2 verschiedenen Statuswerten")
  void shouldCalculateStopPointAccessibilityForSolothurnWithTwoStates() {
    // given
    LocalDate validFrom = LocalDate.of(2020, 1, 1);
    String solothurnSloid = "ch:1:sloid:207";
    String platform1Sloid = "ch:1:sloid:207:1:1";
    String platform2Sloid = "ch:1:sloid:207:2:2";

    // StopPoint
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.buildAutonomyStopPoint(solothurnSloid)
        .validFrom(validFrom)
        .build();

    // Platform 1
    AccessibilityPlatformTestData platform1 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform1Sloid)
        .validFrom(validFrom)
        .build();
    AccessibilityRelationTestData platform1Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform1Sloid)
        .validFrom(validFrom)
        .build();

    // Platform 2
    LocalDate platform2Version1ValidTo = validFrom.plusDays(14);
    AccessibilityPlatformTestData platform2Version1 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform2Sloid)
        .validFrom(validFrom)
        .validTo(platform2Version1ValidTo)
        .build();

    LocalDate platform2Version2ValidFrom = platform2Version1ValidTo.plusDays(1);
    AccessibilityPlatformTestData platform2Version2 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform2Sloid)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE)
        .validFrom(platform2Version2ValidFrom)
        .build();

    AccessibilityRelationTestData platform2Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform2Sloid)
        .validFrom(validFrom)
        .build();

    // Relations
    List<AccessibilityRelationTestData> relations = List.of(platform1Relation, platform2Relation);

    // when
    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(validFrom);
    Accessibility result = WheelchairAccessibility.calculateStopPoint(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(List.of(platform1, platform2Version1, platform2Version2))
            .relations(relations)
            .build(), accessibilityFilter);

    // then
    Accessibility expectedAccessibility = new Accessibility()
        .with(new DateRange(validFrom, platform2Version1ValidTo), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(platform2Version2ValidFrom, validFrom.plusDays(AccessibilityFilter.ACCESSIBILITY_DAYS_TO_CALCULATE)),
            WheelchairAccessibilityState.RAMP_USE);
    assertThat(result).isEqualTo(expectedAccessibility);
  }

  @Test
  @DisplayName("Testcase 2: Wil SG mit 4 Statuswerten und kürzerer Haltestellen-Gültigkeit")
  void shouldCalculateStopPointAccessibilityForWilWithFourStates() {
    // given
    LocalDate validFrom = LocalDate.of(2020, 1, 1);
    List<AccessibilityPlatformTestData> platforms = new ArrayList<>();
    List<AccessibilityRelationTestData> relations = new ArrayList<>();

    // StopPoint
    AccessibilityStopPointTestData stopPoint = AccessibilityStopPointTestData.buildAutonomyStopPoint("ch:1:sloid:6206")
        .validFrom(validFrom)
        .validTo(validFrom.plusDays(24))
        .build();

    // Platform 1
    String platform1Sloid = "ch:1:sloid:6206:1:1";
    AccessibilityPlatformTestData platform1 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform1Sloid)
        .validFrom(validFrom)
        .validTo(validFrom.plusDays(30))
        .build();
    platforms.add(platform1);
    AccessibilityRelationTestData platform1Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform1Sloid)
        .validFrom(platform1.getValidFrom())
        .validTo(platform1.getValidTo())
        .build();
    relations.add(platform1Relation);

    // Platform 11
    String platform11Sloid = "ch:1:sloid:6206:0:11";
    AccessibilityPlatformTestData platform11 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform11Sloid)
        .shuttle(BooleanOptionalAttributeType.YES)
        .validFrom(validFrom.plusDays(19))
        .validTo(validFrom.plusDays(24))
        .build();
    platforms.add(platform11);

    AccessibilityRelationTestData platform11Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform11Sloid)
        .validFrom(platform11.getValidFrom())
        .validTo(platform11.getValidTo())
        .build();
    relations.add(platform11Relation);

    // Platform 12
    String platform12Sloid = "ch:1:sloid:6206:0:12";
    AccessibilityPlatformTestData platform12 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform12Sloid)
        .validFrom(validFrom.plusDays(25))
        .validTo(validFrom.plusDays(30))
        .build();
    platforms.add(platform12);

    AccessibilityRelationTestData platform12Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform12Sloid)
        .stepFreeAccess(StepFreeAccessAttributeType.NO)
        .validFrom(platform12.getValidFrom())
        .validTo(platform12.getValidTo())
        .build();
    relations.add(platform12Relation);

    // Platform 2
    String platform2Sloid = "ch:1:sloid:6206:2:2";
    AccessibilityPlatformTestData platform2 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform2Sloid)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE)
        .validFrom(validFrom.plusDays(6))
        .validTo(validFrom.plusDays(30))
        .build();
    platforms.add(platform2);

    AccessibilityRelationTestData platform2Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform2Sloid)
        .validFrom(platform2.getValidFrom())
        .validTo(platform2.getValidTo())
        .build();
    relations.add(platform2Relation);

    // Platform 43
    String platform43Sloid = "ch:1:sloid:6206:0:43";
    AccessibilityPlatformTestData platform43 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform43Sloid)
        .levelAccessWheelchair(LevelAccessWheelchairAttributeType.NO)
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .validFrom(validFrom.plusDays(12))
        .validTo(validFrom.plusDays(18))
        .build();
    platforms.add(platform43);

    AccessibilityRelationTestData platform43Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform43Sloid)
        .validFrom(platform43.getValidFrom())
        .validTo(platform43.getValidTo())
        .build();
    relations.add(platform43Relation);

    // Platform 3
    String platform3Sloid = "ch:1:sloid:6206:2:3";
    AccessibilityPlatformTestData platform3 = AccessibilityPlatformTestData.buildAutonomyPlatform(platform3Sloid)
        .validFrom(validFrom)
        .validTo(validFrom.plusDays(30))
        .build();
    platforms.add(platform3);

    AccessibilityRelationTestData platform3Relation = AccessibilityRelationTestData.buildAutonomyRelation(platform3Sloid)
        .validFrom(platform3.getValidFrom())
        .validTo(platform3.getValidTo())
        .build();
    relations.add(platform3Relation);

    // when
    AccessibilityFilter accessibilityFilter = new AccessibilityFilter(validFrom);
    Accessibility result = WheelchairAccessibility.calculateStopPoint(
        AccessibilityRequest.builder()
            .stopPoint(List.of(stopPoint))
            .platform(platforms)
            .relations(relations)
            .build(), accessibilityFilter);

    // then
    Accessibility expectedAccessibility = new Accessibility()
        .with(new DateRange(validFrom, validFrom.plusDays(5)), WheelchairAccessibilityState.AUTONOMY)
        .with(new DateRange(validFrom.plusDays(6), validFrom.plusDays(11)), WheelchairAccessibilityState.RAMP_USE)
        .with(new DateRange(validFrom.plusDays(12), validFrom.plusDays(18)), WheelchairAccessibilityState.PRE_REGISTRATION)
        .with(new DateRange(validFrom.plusDays(19), validFrom.plusDays(24)), WheelchairAccessibilityState.SHUTTLE)
        .with(new DateRange(validFrom.plusDays(25), validFrom.plusDays(30)), WheelchairAccessibilityState.NO_INFO);

    assertThat(result).withRepresentation(AccessibilityRepresentation.INSTANCE).isEqualTo(expectedAccessibility);
  }

}
