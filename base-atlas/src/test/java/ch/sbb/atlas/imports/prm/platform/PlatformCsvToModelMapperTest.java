package ch.sbb.atlas.imports.prm.platform;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.testdata.prm.PlatformCsvTestData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformCsvToModelMapperTest {

  @Test
  void shouldMapCsvToCreateModelCorrectly() {
    //given
    PlatformCsvModel csvModel = PlatformCsvTestData.getCsvModel();
    CreatePlatformVersionModel expected = CreatePlatformVersionModel.builder()
        .sloid("ch:1:sloid:76646:0:17")
        .numberWithoutCheckDigit(8576646)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:76646")
        .boardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED)
        .adviceAccessInfo("")
        .contrastingAreas(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .dynamicAudio(BasicAttributeType.TO_BE_COMPLETED)
        .dynamicVisual(BasicAttributeType.TO_BE_COMPLETED)
        .additionalInformation("""
                Blindenquadrat und bodenmarkierter Haltebalken vorhanden,Tramlinie 7 in Fahrtrichtung ( Bümpliz ), Buslinie 12 in Fahrtrichtung Holligen ( Inselspital ) und ( Moonliner 6 /17 18 ) sind ebenfalls aktiv.
                """)
        .height(12.0)
        .inclination(0.0)
        .inclinationLongitudinal(0.0)
        .inclinationWidth(0.0)
        .infoOpportunities(List.of(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION,
            InfoOpportunityAttributeType.ELECTRONIC_VISUAL_INFORMATION_COMPLETE,InfoOpportunityAttributeType.TEXT_TO_SPEECH_COMPLETE))
        .levelAccessWheelchair(BasicAttributeType.TO_BE_COMPLETED)
        .partialElevation(false)
        .superelevation(0.0)
        .tactileSystem(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE)
        .wheelchairAreaLength(300.0)
        .wheelchairAreaWidth(200.0)
        .build();

    CreatePlatformVersionModel result = PlatformCsvToModelMapper.toModel(csvModel);
    assertThat(result).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);
  }


}