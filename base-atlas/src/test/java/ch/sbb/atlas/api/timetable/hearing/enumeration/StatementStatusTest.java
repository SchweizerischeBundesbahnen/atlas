package ch.sbb.atlas.api.timetable.hearing.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatementStatusTest {

  @Test
  void shouldCoverage() {
    assertThat(StatementStatus.values()).hasSize(9);
  }

}