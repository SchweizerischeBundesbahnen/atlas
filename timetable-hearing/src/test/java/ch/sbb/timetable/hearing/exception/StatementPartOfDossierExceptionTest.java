package ch.sbb.timetable.hearing.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.hearing.exception.StatementPartOfDossierException;
import org.junit.jupiter.api.Test;

class StatementPartOfDossierExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    StatementPartOfDossierException exception = new StatementPartOfDossierException();
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "TTH.STATEMENT.PART_OF_DOSSIER");
  }
}