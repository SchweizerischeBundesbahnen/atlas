package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Locks the read-only nature of the Linienmodell 3.0 contract (ATLAS-3573). The write path is out of scope and ships with
 * ATLAS-3578.
 */
class LineApiV3Test {

  private static final List<Class<? extends Annotation>> WRITE_MAPPINGS =
      List.of(PostMapping.class, PutMapping.class, PatchMapping.class, DeleteMapping.class);

  @Test
  void shouldDeclareExactlyTwoReadOperations() {
    //when
    List<Method> methods = Arrays.stream(LineApiV3.class.getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .toList();

    //then
    assertThat(methods).hasSize(2);
    assertThat(methods).map(Method::getName)
        .containsExactlyInAnyOrder("getLineVersions", "getLineVersionsV3");
    assertThat(methods).allSatisfy(method -> assertThat(method.getAnnotation(GetMapping.class)).isNotNull());
  }

  @Test
  void shouldNotDeclareAnyWriteMapping() {
    //then
    for (Method method : LineApiV3.class.getDeclaredMethods()) {
      for (Class<? extends Annotation> writeMapping : WRITE_MAPPINGS) {
        assertThat(method.getAnnotation(writeMapping))
            .withFailMessage("%s must not carry %s", method.getName(), writeMapping.getSimpleName())
            .isNull();
      }
    }
  }

  @Test
  void shouldBeMappedToV3LinesRoot() {
    //when
    RequestMapping requestMapping = LineApiV3.class.getAnnotation(RequestMapping.class);

    //then
    assertThat(requestMapping).isNotNull();
    assertThat(requestMapping.value()).containsExactly("v3/lines");
  }

  @Test
  void shouldNotExtendBaseLineVersionModelToKeepWorkflowsOut() {
    //then
    assertThat(BaseLineVersionModel.class.isAssignableFrom(LineVersionModelV3.class)).isFalse();
    assertThat(Arrays.stream(LineVersionModelV3.class.getDeclaredFields()).map(Field::getName))
        .doesNotContain("lineVersionWorkflows", "shortNumber");
  }

}
