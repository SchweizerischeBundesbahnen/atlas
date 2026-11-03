package ch.sbb.line.directory.module.line.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3FieldPermissionException;
import ch.sbb.line.directory.module.line.security.LineV3FieldPermissionService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineV3FieldPermissionValidatorTest {

  @Mock
  private LineV3FieldPermissionService fieldPermissionService;

  @InjectMocks
  private LineV3FieldPermissionValidator fieldPermissionValidator;

  @Test
  void shouldRejectCreationWithForbiddenFields() {
    // Given
    LineVersionV3 cableway = LineVersionV3TestData.cableway();
    when(fieldPermissionService.forbiddenFieldsOnCreate(cableway)).thenReturn(Set.of("financing"));

    // When
    Throwable thrown = catchThrowable(() -> fieldPermissionValidator.validateCreate(cableway));

    // Then
    assertThat(thrown).isInstanceOf(LineV3FieldPermissionException.class);
    assertThat(((LineV3FieldPermissionException) thrown).getFields()).containsExactly("financing");
  }

  @Test
  void shouldAcceptCreationWithoutForbiddenFields() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    when(fieldPermissionService.forbiddenFieldsOnCreate(line)).thenReturn(Set.of());

    // When & Then
    assertThatCode(() -> fieldPermissionValidator.validateCreate(line)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectUpdateWithForbiddenFields() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().description("changed").build();
    when(fieldPermissionService.forbiddenFieldsOnUpdate(current, edited)).thenReturn(Set.of("cablewayName"));

    // When
    Throwable thrown = catchThrowable(() -> fieldPermissionValidator.validateUpdate(current, edited));

    // Then
    assertThat(thrown).isInstanceOf(LineV3FieldPermissionException.class);
    assertThat(((LineV3FieldPermissionException) thrown).getFields()).containsExactly("cablewayName");
  }

}
