package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointTemplateGenerator {

  private static final String DEFAULT_SLOID = "ch:1:sloid:7000:23";
  private static final int DEFAULT_VALID_FROM_YEAR = 2021;
  private static final int DEFAULT_VALID_FROM_MONTH = 4;
  private static final int DEFAULT_VALID_FROM_DAY = 1;
  private static final int DEFAULT_VALID_TO_YEAR = 2099;
  private static final int DEFAULT_VALID_TO_MONTH = 12;
  private static final int DEFAULT_VALID_TO_DAY = 31;
  private static final String DEFAULT_DESIGNATION = "Perron 3";
  private static final String DEFAULT_DESIGNATION_OPEATIONAL = "CAMPSTR2";
  private static final Double DEFAULT_LENGTH = 12.000;
  private static final Double DEFAULT_BOARDING_AREA_HEIGHT = 16.00;
  private static final Double DEFAULT_COMPASS_DIRECTION = 278.00;
  private static final Double DEFAULT_EAST = 2600037.945;
  private static final Double DEFAULT_NORTH = 1199749.812;
  private static final Double DEFAULT_HEIGHT = 540.2;
  private static final String DEFAULT_PARENT_SLOID = "ch:1:sloid:7000";

  public static TrafficPointUpdateCsvModel getTrafficPointUpdateCsvModelExample() {
    return TrafficPointUpdateCsvModel.builder()
        .sloid(DEFAULT_SLOID)
        .validFrom(LocalDate.of(DEFAULT_VALID_FROM_YEAR, DEFAULT_VALID_FROM_MONTH, DEFAULT_VALID_FROM_DAY))
        .validTo(LocalDate.of(DEFAULT_VALID_TO_YEAR, DEFAULT_VALID_TO_MONTH, DEFAULT_VALID_TO_DAY))
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
