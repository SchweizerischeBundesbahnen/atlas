package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.BaseValidatorTest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class BaseLineVersionModelV3Test extends BaseValidatorTest {

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> createModel() {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .description("description")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31));
  }

  private static UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModel() {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(1)
        .description("description")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31));
  }

  @Test
  void shouldDocumentFotCommentAsWritable() throws NoSuchFieldException {
    // When
    Schema schema = BaseLineVersionModelV3.class.getDeclaredField(BaseLineVersionModelV3.Fields.fotComment)
        .getAnnotation(Schema.class);

    // Then
    assertThat(schema.accessMode()).isNotEqualTo(AccessMode.READ_ONLY);
    assertThat(schema.description()).contains("Supervisor");
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"BAV comment", "test", "äöü ÄÖÜ é", "a b"})
  void shouldAcceptNullOrTrimmedFotCommentOnCreateAndUpdate(String fotComment) {
    // When
    Set<ConstraintViolation<CreateLineVersionModelV3>> createViolations = validator.validate(
        createModel().fotComment(fotComment).build());
    Set<ConstraintViolation<UpdateLineVersionModelV3>> updateViolations = validator.validate(
        updateModel().fotComment(fotComment).build());

    // Then
    assertThat(createViolations).isEmpty();
    assertThat(updateViolations).isEmpty();
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {" ", "   ", "\t\n", " test", "test ", " test "})
  void shouldRejectBlankOrUntrimmedFotCommentOnCreateAndUpdate(String fotComment) {
    // When / Then
    assertThat(validator.validate(createModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
    assertThat(validator.validate(updateModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
  }

  @Test
  void shouldAcceptFotCommentOfMaxLength() {
    // Given
    String fotComment = "b".repeat(AtlasFieldLengths.LENGTH_1500);

    // When / Then
    assertThat(validator.validate(createModel().fotComment(fotComment).build())).isEmpty();
    assertThat(validator.validate(updateModel().fotComment(fotComment).build())).isEmpty();
  }

  @Test
  void shouldRejectFotCommentExceedingMaxLength() {
    // Given
    String fotComment = "b".repeat(AtlasFieldLengths.LENGTH_1500 + 1);

    // When / Then
    assertThat(validator.validate(createModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
    assertThat(validator.validate(updateModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
  }

  @Test
  void shouldRejectFotCommentOutsideIso88591() {
    // Given
    String fotComment = "Preis in \u20AC";

    // When / Then
    assertThat(validator.validate(createModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
    assertThat(validator.validate(updateModel().fotComment(fotComment).build())).extracting(this::property)
        .containsExactly(BaseLineVersionModelV3.Fields.fotComment);
  }

  private String property(ConstraintViolation<?> violation) {
    return violation.getPropertyPath().toString();
  }

}
