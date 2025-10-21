package ch.sbb.atlas.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import org.junit.jupiter.api.Test;

public class ValidTtfnDescriptionValidatorTest {

  private final ValidTtfnDescription.Validator validator = new ValidTtfnDescription.Validator();

  @Test
  void emptySecondAndThirdLines_areValidRegardlessOfFirst() {
    assertTrue(validator.isValid(model("a", null, null, "abc", null, null), null));
  }

  @Test
  void outwardLine2Filled_requiresOutwardLine1AtLeastTwoChars() {
    assertFalse(validator.isValid(model(null, "B", null, null, null, null), null));
    assertFalse(validator.isValid(model("A", "B", null, null, null, null), null));
    assertTrue(validator.isValid(model("AB", "B", null, null, null, null), null));
  }

  @Test
  void outwardLine3Filled_requiresOutwardLine2AtLeastTwoChars_andThusLine1AtLeastTwoChars() {
    assertFalse(validator.isValid(model("AB", null, "C", null, null, null), null));
    assertFalse(validator.isValid(model("AB", "X", "C", null, null, null), null));
    assertTrue(validator.isValid(model("AB", "CD", "E", null, null, null), null));
    assertFalse(validator.isValid(model("A", "CD", "E", null, null, null), null));
    assertFalse(validator.isValid(model(null, "CD", "E", null, null, null), null));
  }

  @Test
  void returnLine2Filled_requiresReturnLine1AtLeastTwoChars() {
    assertFalse(validator.isValid(model(null, null, null, null, "Y", null), null));
    assertFalse(validator.isValid(model(null, null, null, "Z", "Y", null), null));
    assertTrue(validator.isValid(model(null, null, null, "ZZ", "Y", null), null));
  }

  @Test
  void returnLine3Filled_requiresReturnLine2AtLeastTwoChars_andThusReturnLine1AtLeastTwoChars() {
    assertFalse(validator.isValid(model(null, null, null, "RR", null, "S"), null));
    assertFalse(validator.isValid(model(null, null, null, "RR", "R", "S"), null));
    assertTrue(validator.isValid(model(null, null, null, "RR", "QS", "S"), null));
    assertFalse(validator.isValid(model(null, null, null, "R", "SS", "T"), null));
    assertFalse(validator.isValid(model(null, null, null, null, "SS", "T"), null));
  }

  private static TimetableFieldNumberVersionModel model(String outward1, String outward2, String outward3, String ret1,
      String ret2, String ret3) {
    TimetableFieldNumberVersionModel m = mock(TimetableFieldNumberVersionModel.class);
    when(m.getDescriptionOutwardLine1()).thenReturn(outward1);
    when(m.getDescriptionOutwardLine2()).thenReturn(outward2);
    when(m.getDescriptionOutwardLine3()).thenReturn(outward3);
    when(m.getDescriptionReturnLine1()).thenReturn(ret1);
    when(m.getDescriptionReturnLine2()).thenReturn(ret2);
    when(m.getDescriptionReturnLine3()).thenReturn(ret3);
    return m;
  }
}
