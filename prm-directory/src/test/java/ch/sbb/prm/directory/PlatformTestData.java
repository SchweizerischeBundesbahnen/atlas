package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.InfoOpportunityType;
import ch.sbb.prm.directory.enumeration.VehicleAccessType;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformTestData {

  public static PlatformVersion getPlatformVersion(){
    return PlatformVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .boardingDevice(BoardingDeviceType.LIFTS)
        .adviceAccessInfo("Access Information Advice")
        .contrastingAreas(BooleanOptionalAttributeType.YES)
        .dynamicAudio(BasicAttributeType.NOT_APPLICABLE)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .height(123.12)
        .inclination(123.12)
        .inclinationLongitudinal(123.12)
        .inclinationWidth(123.12)
        .infoOpportunities(Set.of(InfoOpportunityType.ACOUSTIC_INFORMATION,
            InfoOpportunityType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE,InfoOpportunityType.TEXT_TO_SPEECH_DEPARTURES))
        .levelAccessWheelchair(BasicAttributeType.NO)
        .partialElevation(BooleanAttributeType.NO)
        .superelevation(321.123)
        .tactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED)
        .wheelchairAreaLength(456.321)
        .wheelchairAreaWidth(321.123)
        .build();

  }

}
