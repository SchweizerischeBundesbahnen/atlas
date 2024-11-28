package ch.sbb.atlas.versioning.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;

 class ReflectionHelperTest {

  @Test
  void shouldGetAllFieldsIncludingSuperclassProperties() {
    // Given

    // When
    List<Field> fields = ReflectionHelper.getAllFieldsAccessible(ChildVersionable.class);

    // Then
    assertThat(fields).extracting("name").contains("property", "additionalProperty");
  }

  @Test
  void shouldGetExistingChildFieldIncludingSuperclassProperties() throws NoSuchFieldException {
    // Given
    String childProperty = "property";

    // When
    Field field = ReflectionHelper.getFieldAccessible(ChildVersionable.class, childProperty);

    // Then
    assertThat(field).isNotNull();
    assertThat(field.getName()).isEqualTo(childProperty);
  }

  @Test
  void shouldGetExistingParentFieldIncludingSuperclassProperties() throws NoSuchFieldException {
    // Given
    String parentProperty = "additionalProperty";

    // When
    Field field = ReflectionHelper.getFieldAccessible(ChildVersionable.class, parentProperty);

    // Then
    assertThat(field).isNotNull();
    assertThat(field.getName()).isEqualTo(parentProperty);
  }

  @Test
  void shouldThrowExceptionOnNotExistingField() {
    // Given

    // When
    assertThatThrownBy(
        () -> ReflectionHelper.getFieldAccessible(ChildVersionable.class, "pizza")).isInstanceOf(
        NoSuchFieldException.class);

    // Then
  }

   @Test
   void shouldCopyObjectViaBuilder() {
     // Given
     ObjectWithToBuilder original = ObjectWithToBuilder.builder().property("original").build();

     // When
     Object copy = ReflectionHelper.copyObjectViaBuilder(original);

     // Then
     assertThat(copy).isNotSameAs(original).isEqualTo(original);
   }

   @Test
   void shouldThrowExceptionIfToBuilderIsNotPresent() {
     // Given
     ParentVersionable original = new ParentVersionable();

     // When
     assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> ReflectionHelper.copyObjectViaBuilder(original));
   }

  private static class ParentVersionable {

    private String additionalProperty;
  }

  private static class ChildVersionable extends ParentVersionable {

    private String property;
  }

  @Data
  @Builder(toBuilder = true)
  private static class ObjectWithToBuilder{
    private String property;
  }
}