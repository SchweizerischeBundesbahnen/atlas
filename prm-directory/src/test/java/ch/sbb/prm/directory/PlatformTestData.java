package ch.sbb.prm.directory;

import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.ACOUSTIC_INFORMATION;
import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE;
import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.TEXT_TO_SPEECH_DEPARTURES;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.PlatformVersion.PlatformVersionBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformTestData {

  public static PlatformVersion getPlatformVersion() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .editionDate(LocalDateTime.now())
        .creationDate(LocalDateTime.now())
        .creator("u123456")
        .editor("u123456")
        .build();

  }

  public static PlatformVersion getCompletePlatformVersion() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .inclination(123.12)
        .inclinationWidth(123.12)
        .levelAccessWheelchair(BasicAttributeType.NO)
        .superelevation(321.123)
        .build();

  }

  public static PlatformVersion getReducedPlatformVersion() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .height(123.12)
        .inclinationLongitudinal(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

  public static PlatformVersionModel getPlatformVersionModel() {
    return PlatformVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .additionalInformation("additional info")
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(List.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

  public static PlatformVersionModel getCreateCompletePlatformVersionModel() {
    return PlatformVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .additionalInformation("additional info")
        .inclination(123.12)
        .inclinationWidth(123.12)
        .levelAccessWheelchair(BasicAttributeType.NO)
        .superelevation(321.123)
        .build();

  }

  public static PlatformVersionModel getCreateReducedPlatformVersionModel() {
    return PlatformVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .height(333.12)
        .inclinationLongitudinal(123.12)
        .infoOpportunities(List.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

  public static PlatformVersionBuilder<?, ?> builderVersion1() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional")
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderCompleteVersion1() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional 1")
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .inclination(123.12)
        .inclinationWidth(123.12)
        .levelAccessWheelchair(BasicAttributeType.NO)
        .superelevation(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderCompleteVersion2() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional 2")
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .inclination(123.12)
        .inclinationWidth(123.12)
        .levelAccessWheelchair(BasicAttributeType.NO)
        .superelevation(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderCompleteVersion3() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional")
        .adviceAccessInfo("yes Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .inclination(123.12)
        .inclinationWidth(123.12)
        .levelAccessWheelchair(BasicAttributeType.NO)
        .superelevation(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderVersion2() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional")
        .adviceAccessInfo("No Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(Set.of(InfoOpportunityAttributeType.TO_BE_COMPLETED))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderVersion3() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .additionalInformation("additional")
        .adviceAccessInfo("yes Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(false)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderReducedVersion1() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .height(123.12)
        .inclinationLongitudinal(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);

  }

  public static PlatformVersionBuilder<?, ?> builderReducedVersion2() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .height(111.12)
        .inclinationLongitudinal(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);
  }

  public static PlatformVersionBuilder<?, ?> builderReducedVersion3() {
    return PlatformVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .height(333.12)
        .inclinationLongitudinal(123.12)
        .infoOpportunities(Set.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123);
  }

}
