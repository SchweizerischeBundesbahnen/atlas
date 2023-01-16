package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

class OperatingPointTypeTest {

  @Test
  void shouldHaveTimetable() {
    OperatingPointTypes.TYPES_WITHOUT_TIMETABLE.forEach(
        operatingPointType -> assertThat(operatingPointType.hasTimetable()).isFalse());
  }

  @Test
  void shouldNotHaveTimetable() {
    OperatingPointTypes.TYPES_WITH_TIMETABLE.forEach(operatingPointType -> assertThat(operatingPointType.hasTimetable()).isTrue());
  }
}