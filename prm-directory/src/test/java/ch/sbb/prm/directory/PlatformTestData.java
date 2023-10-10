package ch.sbb.prm.directory;

import static ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType.ACOUSTIC_INFORMATION;
import static ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE;
import static ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType.TEXT_TO_SPEECH_DEPARTURES;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.platform.CreatePlatformVersionModel;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.VehicleAccessAttributeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformTestData {

  public static PlatformVersion getPlatformVersion() {
    return PlatformVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
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
        .partialElevation(BooleanAttributeType.NO)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

  public static CreatePlatformVersionModel getCreatePlatformVersionModel() {
    return CreatePlatformVersionModel.builder()
        .sloid("ch:1.sloid:12345:1")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .boardingDevice(BoardingDeviceAttributeType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .additionalInfo("additional info")
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(List.of(ACOUSTIC_INFORMATION, ELECTRONIC_VISUAL_INFORMATION_COMPLETE, TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(BooleanAttributeType.NO)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

}
