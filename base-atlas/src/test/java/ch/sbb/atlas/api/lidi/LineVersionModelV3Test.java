package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.BaseValidatorTest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LineVersionModelV3Test extends BaseValidatorTest {

  @Test
  void shouldDocumentFotCommentAsReadOnly() throws NoSuchFieldException {
    //when
    Schema schema = LineVersionModelV3.class.getDeclaredField(LineVersionModelV3.Fields.fotComment).getAnnotation(Schema.class);

    //then
    assertThat(schema.accessMode()).isEqualTo(AccessMode.READ_ONLY);
  }

  @Test
  void shouldAcceptFotCommentOfMaxLength() {
    //given
    LineVersionModelV3 model = LineVersionModelV3.builder().fotComment("b".repeat(AtlasFieldLengths.LENGTH_1500)).build();

    //when
    Set<ConstraintViolation<LineVersionModelV3>> violations = validator.validateProperty(model, LineVersionModelV3.Fields.fotComment);

    //then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRejectFotCommentExceedingMaxLength() {
    //given
    LineVersionModelV3 model = LineVersionModelV3.builder().fotComment("b".repeat(AtlasFieldLengths.LENGTH_1500 + 1)).build();

    //when
    Set<ConstraintViolation<LineVersionModelV3>> violations = validator.validateProperty(model, LineVersionModelV3.Fields.fotComment);

    //then
    assertThat(violations).hasSize(1);
  }

  @Test
  void shouldRejectFotCommentOutsideIso88591() {
    //given
    LineVersionModelV3 model = LineVersionModelV3.builder().fotComment("\u4e2d").build();

    //when
    Set<ConstraintViolation<LineVersionModelV3>> violations = validator.validateProperty(model, LineVersionModelV3.Fields.fotComment);

    //then
    assertThat(violations).hasSize(1);
  }

}
