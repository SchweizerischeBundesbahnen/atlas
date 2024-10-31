package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointTemplateGenerator {

  private static final String DEFAULT_SLOID = "ch:1:sloid:7000:1:2";
  private static final LocalDate DEFAULT_VALID_FROM = LocalDate.of(2021, 4, 1);
  private static final LocalDate DEFAULT_VALID_TO = LocalDate.of(2099, 12, 31);
  private static final String DEFAULT_DESIGNATION = "Perron 3";
  private static final String DEFAULT_DESIGNATION_OPEATIONAL = "CAMPSTR2";
  private static final Double DEFAULT_LENGTH = 12.000;
  private static final Double DEFAULT_BOARDING_AREA_HEIGHT = 16.00;
  private static final Double DEFAULT_COMPASS_DIRECTION = 278.00;
  private static final Double DEFAULT_EAST = 2600037.945;
  private static final Double DEFAULT_NORTH = 1199749.812;
  private static final Double DEFAULT_HEIGHT = 540.2;
  private static final String DEFAULT_PARENT_SLOID = "ch:1:sloid:7000:1";

  public static TrafficPointUpdateCsvModel getTrafficPointUpdateCsvModelExample() {
    return TrafficPointUpdateCsvModel.builder()
        .sloid(DEFAULT_SLOID)
        .validFrom(DEFAULT_VALID_FROM)
        .validTo(DEFAULT_VALID_TO)
        .designation(DEFAULT_DESIGNATION)
        .designationOperational(DEFAULT_DESIGNATION_OPEATIONAL)
        .length(DEFAULT_LENGTH)
        .boardingAreaHeight(DEFAULT_BOARDING_AREA_HEIGHT)
        .compassDirection(DEFAULT_COMPASS_DIRECTION)
        .east(DEFAULT_EAST)
        .north(DEFAULT_NORTH)
        .spatialReference(SpatialReference.LV95)
        .height(DEFAULT_HEIGHT)
        .parentSloid(DEFAULT_PARENT_SLOID)
        .build();
  }

}
