package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
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

}
