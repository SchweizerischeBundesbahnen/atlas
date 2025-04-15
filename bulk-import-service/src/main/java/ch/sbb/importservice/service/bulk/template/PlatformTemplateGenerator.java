package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformTemplateGenerator {

  public static PlatformReducedUpdateCsvModel getPlatformReducedUpdateCsvModelExample() {
    return PlatformReducedUpdateCsvModel.builder()
        .sloid("ch:1:sloid:88253:0:1")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .additionalInformation("Die Buslinie 160 Fahrtrichtung Münsingen Bahnhof Konolfingen Dorf bedienen diese Haltekante.")
        .height(16.0)
        .inclinationLongitudinal(2.0)
        .infoOpportunities(Set.of(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION))
        .partialElevation(false)
        .tactileSystem(BooleanOptionalAttributeType.NO)
        .attentionField(BooleanOptionalAttributeType.NO)
        .vehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED)
        .wheelchairAreaLength(300.0)
        .wheelchairAreaWidth(257.0)
        .build();
  }

}
