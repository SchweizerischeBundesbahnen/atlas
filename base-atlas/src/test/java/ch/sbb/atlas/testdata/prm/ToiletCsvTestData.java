package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToiletCsvTestData {

  public static ToiletCsvModelContainer getContainer() {
    return ToiletCsvModelContainer.builder()
        .sloid(getCsvModel().getSloid())
        .csvModels(List.of(getCsvModel()))
        .build();
  }

  public static ToiletCsvModel getCsvModel() {
    return ToiletCsvModel.builder()
        .sloid("ch:1:sloid:76646:0:17")
        .dsSloid("ch:1:sloid:76646")
        .didokCode(85766469)
        .status(1)
        .info("""
                Blindenquadrat und bodenmarkierter Haltebalken vorhanden,Tramlinie 7 in Fahrtrichtung ( Bümpliz ), Buslinie 12 in Fahrtrichtung Holligen ( Inselspital ) und ( Moonliner 6 /17 18 ) sind ebenfalls aktiv.
                """)
        .description("Desc")
        .wheelchairToilet(2)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();
  }

}
