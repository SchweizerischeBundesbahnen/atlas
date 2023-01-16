package ch.sbb.atlas.servicepointdirectory.enumeration;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperatingPointTypes {

  private static final EnumSet<OperatingPointType> WITH_TIMETABLE = EnumSet.of(OperatingPointType.INVENTORY_POINT,
      OperatingPointType.SYSTEM_OPERATING_POINT,
      OperatingPointType.RAILNET_POINT,
      OperatingPointType.ROUTE_SPEED_CHANGE);
  public static final Set<OperatingPointType> TYPES_WITHOUT_TIMETABLE = Collections.unmodifiableSet(WITH_TIMETABLE);
  public static final Set<OperatingPointType> TYPES_WITH_TIMETABLE = Collections.unmodifiableSet(
      EnumSet.complementOf(WITH_TIMETABLE));
}
