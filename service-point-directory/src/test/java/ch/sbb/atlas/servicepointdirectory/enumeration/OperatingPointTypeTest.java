package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

class OperatingPointTypeTest {

  @Test
  void shouldHaveTimetable() {
    OperatingPointType.TYPES_WITHOUT_TIMETABLE.forEach(
        operatingPointType -> assertThat(operatingPointType.hasTimetable()).isFalse());
  }

  @Test
  void shouldNotHaveTimetable() {
    OperatingPointType.TYPES_WITH_TIMETABLE.forEach(operatingPointType -> assertThat(operatingPointType.hasTimetable()).isTrue());
  }
}