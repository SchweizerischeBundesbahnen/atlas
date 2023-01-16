package ch.sbb.atlas.servicepointdirectory.enumeration;

import java.util.EnumSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperatingPointTypes {

  public static final EnumSet<OperatingPointType> TYPES_WITHOUT_TIMETABLE = EnumSet.of(OperatingPointType.INVENTORY_POINT,
      OperatingPointType.SYSTEM_OPERATING_POINT,
      OperatingPointType.RAILNET_POINT,
      OperatingPointType.ROUTE_SPEED_CHANGE);
  public static final EnumSet<OperatingPointType> TYPES_WITH_TIMETABLE = EnumSet.complementOf(TYPES_WITHOUT_TIMETABLE);
}
