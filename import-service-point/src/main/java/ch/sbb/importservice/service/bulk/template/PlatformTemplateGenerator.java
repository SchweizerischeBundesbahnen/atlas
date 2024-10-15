package ch.sbb.importservice.service.bulk.template;

import static ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION;
import static ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType.TO_BE_COMPLETED;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformTemplateGenerator {

  private static final String DEFAULT_SLOID = "ch:1:sloid:88253:0:1";
  private static final LocalDate DEFAULT_VALID_FROM = LocalDate.of(2021, 4, 1);
  private static final LocalDate DEFAULT_VALID_TO = LocalDate.of(2099, 12, 31);
  private static final String DEFAULT_ADDITIONAL_INFORMATION = "Die Buslinie 160 Fahrtrichtung Münsingen Bahnhof Konolfingen "
      + "Dorf bedienen diese Haltekante.";
  private static final Double DEFAULT_HEIGHT = 16.000;
  private static final Double DEFAULT_INCLINATION_LOGNITUDINAL = 2.000;
  private static final Set<InfoOpportunityAttributeType> DEFAULT_INFO_OPPORTUNITY_ATTRIBUTE_TYPE =
      Set.of(STATIC_VISUAL_INFORMATION);
  private static final Boolean DEFAULT_PARTIAL_ELEVATION = false;
  private static final BooleanOptionalAttributeType DEFAULT_TACTILE_SYSTEM = BooleanOptionalAttributeType.NO;
  private static final VehicleAccessAttributeType DEFAULT_VEHICLE_ACCESS_ATTRIBUTE_TYPE = TO_BE_COMPLETED;
  private static final Double DEFAULT_WHEELCHAIR_AREA_LENGTH = 300.000;
  private static final Double DEFAULT_WHEELCHAIR_AREA_WIDTH = 257.000;

  public static PlatformUpdateCsvModel getPlatformUpdateCsvModelExample() {
    return PlatformUpdateCsvModel.builder()
        .sloid(DEFAULT_SLOID)
        .validFrom(DEFAULT_VALID_FROM)
        .validTo(DEFAULT_VALID_TO)
        .additionalInformation(DEFAULT_ADDITIONAL_INFORMATION)
        .height(DEFAULT_HEIGHT)
        .inclinationLongitudinal(DEFAULT_INCLINATION_LOGNITUDINAL)
        .infoOpportunities(DEFAULT_INFO_OPPORTUNITY_ATTRIBUTE_TYPE)
        .partialElevation(DEFAULT_PARTIAL_ELEVATION)
        .tactileSystem(DEFAULT_TACTILE_SYSTEM)
        .vehicleAccess(DEFAULT_VEHICLE_ACCESS_ATTRIBUTE_TYPE)
        .wheelchairAreaLength(DEFAULT_WHEELCHAIR_AREA_LENGTH)
        .wheelchairAreaWidth(DEFAULT_WHEELCHAIR_AREA_WIDTH)
        .build();
  }

}
