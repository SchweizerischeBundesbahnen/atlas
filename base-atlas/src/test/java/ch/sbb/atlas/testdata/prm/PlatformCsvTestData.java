package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformCsvTestData {

  public static PlatformCsvModelContainer getContainer() {
    return PlatformCsvModelContainer.builder()
        .sloid(getCsvModel().getSloid())
        .csvModels(List.of(getCsvModel()))
        .build();
  }

  public static PlatformCsvModel getCsvModel() {
    return PlatformCsvModel.builder()
        .sloid("ch:1:sloid:76646:0:17")
        .dsSloid("ch:1:sloid:76646")
        .didokCode(85766469)
        .status(1)
        .boardingDevice(0)
        .accessInfo("")
        .infos("""
                Blindenquadrat und bodenmarkierter Haltebalken vorhanden,Tramlinie 7 in Fahrtrichtung ( Bümpliz ), Buslinie 12 in Fahrtrichtung Holligen ( Inselspital ) und ( Moonliner 6 /17 18 ) sind ebenfalls aktiv.
                """)
        .contrastingAreas(0)
        .dynamicAudio(0)
        .dynamicVisual(0)
        .height(12.0)
        .inclination(0.0)
        .inclinationLong(0.0)
        .inclinationWidth(0.0)
        .infoBlinds("~15~17~20~")
        .levelAccessWheelchair(0)
        .partialElev(0)
        .superelevation(0.0)
        .tactileSystems(2)
        .vehicleAccess(12)
        .wheelchairAreaLength(300.0)
        .wheelchairAreaWidth(200.0)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();
  }

}
