package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class OperatingPointTypeTest {

  private static final EnumSet<OperatingPointType> TYPES_WITHOUT_TIMETABLE = EnumSet.of(OperatingPointType.INVENTORY_POINT,
      OperatingPointType.SYSTEM_OPERATING_POINT,
      OperatingPointType.RAILNET_POINT,
      OperatingPointType.ROUTE_SPEED_CHANGE);

  @Test
  void shouldHaveTimetable() {
    TYPES_WITHOUT_TIMETABLE.forEach(operatingPointType -> assertThat(operatingPointType.hasTimetable()).isFalse());
  }

  @Test
  void shouldNotHaveTimetable() {
    EnumSet.complementOf(TYPES_WITHOUT_TIMETABLE)
        .forEach(operatingPointType -> assertThat(operatingPointType.hasTimetable()).isTrue());
  }
}