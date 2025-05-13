package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointConstants {

  public static final Set<Country> AUTOMATIC_SERVICE_POINT_ID = Set.of(
      Country.SWITZERLAND,
      Country.GERMANY_BUS,
      Country.AUSTRIA_BUS,
      Country.ITALY_BUS,
      Country.FRANCE_BUS
  );

  public static final long LOADING_POINT_NUMBER_MIN = 0;
  public static final long LOADING_POINT_NUMBER_MAX = 9999;

  public static final String ALLIANCE_SWISS_PASS_SBOID = "ch:1:sboid:101704";
  public static final LocalDate ATLAS_MIGRATION_DATE = LocalDate.of(2024, 4, 15);
}
