package ch.sbb.atlas.validation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import org.junit.jupiter.api.Test;

class ValidTtfnDescriptionValidatorTest {

  private final ValidTtfnDescription.Validator validator = new ValidTtfnDescription.Validator();

  @Test
  void shouldTreatEmptySecondAndThirdLinesAsValidRegardlessOfFirst() {
    assertThat(validator.isValid(model("a", null, null, "abc", null, null), null)).isTrue();
  }

  @Test
  void shouldRequireOutwardLine1AtLeastTwoCharsWhenOutwardLine2Filled() {
    assertThat(validator.isValid(model(null, "B", null, null, null, null), null)).isFalse();
    assertThat(validator.isValid(model("A", "B", null, null, null, null), null)).isFalse();
    assertThat(validator.isValid(model("AB", "B", null, null, null, null), null)).isTrue();
  }

  @Test
  void shouldRequireOutwardLine1And2AtLeastTwoCharsWhenOutwardLine3Filled() {
    assertThat(validator.isValid(model("AB", null, "C", null, null, null), null)).isFalse();
    assertThat(validator.isValid(model("AB", "X", "C", null, null, null), null)).isFalse();
    assertThat(validator.isValid(model("AB", "CD", "E", null, null, null), null)).isTrue();
    assertThat(validator.isValid(model("A", "CD", "E", null, null, null), null)).isFalse();
    assertThat(validator.isValid(model(null, "CD", "E", null, null, null), null)).isFalse();
  }

  @Test
  void shouldRequireReturnLine1AtLeastTwoCharsWhenReturnLine2Filled() {
    assertThat(validator.isValid(model(null, null, null, null, "Y", null), null)).isFalse();
    assertThat(validator.isValid(model(null, null, null, "Z", "Y", null), null)).isFalse();
    assertThat(validator.isValid(model(null, null, null, "ZZ", "Y", null), null)).isTrue();
  }

  @Test
  void shouldRequireReturnLine1And2AtLeastTwoCharsWhenReturnLine3Filled() {
    assertThat(validator.isValid(model(null, null, null, "RR", null, "S"), null)).isFalse();
    assertThat(validator.isValid(model(null, null, null, "RR", "R", "S"), null)).isFalse();
    assertThat(validator.isValid(model(null, null, null, "RR", "QS", "S"), null)).isTrue();
    assertThat(validator.isValid(model(null, null, null, "R", "SS", "T"), null)).isFalse();
    assertThat(validator.isValid(model(null, null, null, null, "SS", "T"), null)).isFalse();
  }

  private static TimetableFieldNumberVersionModel model(
      String outward1, String outward2, String outward3, String ret1, String ret2, String ret3) {
    return TimetableFieldNumberVersionModel.builder()
        .descriptionOutwardLine1(outward1)
        .descriptionOutwardLine2(outward2)
        .descriptionOutwardLine3(outward3)
        .descriptionReturnLine1(ret1)
        .descriptionReturnLine2(ret2)
        .descriptionReturnLine3(ret3)
        .build();
  }
}
