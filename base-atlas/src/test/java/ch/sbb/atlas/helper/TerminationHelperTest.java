package ch.sbb.atlas.helper;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.exception.TerminationNotAllowedValidToNotWithinLastVersionRangeException;
import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TerminationHelperTest {

  @Test
  void shouldNotThrowIfValidToWithinRange() {
    String sloid = "test-id";
    LocalDate from = LocalDate.of(2024, 1, 1);
    LocalDate to = LocalDate.of(2024, 12, 31);
    DateRange dateRange = new DateRange(from, to);
    LocalDate validTo = LocalDate.of(2024, 6, 1);

    assertDoesNotThrow(() ->
        TerminationHelper.isValidToInLastVersionRange(sloid, dateRange, validTo)
    );
  }

  @Test
  void shouldThrowIfValidToOutsideRange() {
    String sloid = "test-id";
    LocalDate from = LocalDate.of(2024, 1, 1);
    LocalDate to = LocalDate.of(2024, 12, 31);
    DateRange dateRange = new DateRange(from, to);
    LocalDate validTo = LocalDate.of(2025, 1, 1);

    assertThrows(TerminationNotAllowedValidToNotWithinLastVersionRangeException.class, () ->
        TerminationHelper.isValidToInLastVersionRange(sloid, dateRange, validTo)
    );
  }

}
