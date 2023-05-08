package ch.sbb.atlas.api.timetable.hearing.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StatementStatusTest {

  @Test
  public void shouldCoverage() {
    assertThat(StatementStatus.values().length).isEqualTo(9);
  }

}