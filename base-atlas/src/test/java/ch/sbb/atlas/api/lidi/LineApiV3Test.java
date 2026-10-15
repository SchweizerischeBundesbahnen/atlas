package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class LineApiV3Test {

  @Test
  void shouldDocumentEveryStatusReturnedWhenCreatingLineVersion() throws Exception {
    // Given
    Method create = LineApiV3.class.getMethod("createLineVersionV3", CreateLineVersionModelV3.class);

    // When
    Map<String, String> responses = documentedResponses(create);

    // Then
    assertThat(responses).containsOnlyKeys("201", "400", "403", "404", "409", "422");
  }

  @Test
  void shouldDocumentReferencedLineRevokedAndMemberValidityAsConflictWhenCreatingLineVersion() throws Exception {
    // Given
    Method create = LineApiV3.class.getMethod("createLineVersionV3", CreateLineVersionModelV3.class);

    // When
    Map<String, String> responses = documentedResponses(create);

    // Then
    assertThat(responses.get("409")).contains("referenced line is REVOKED", "outside of the referenced line validity");
    assertThat(responses.get("403")).doesNotContain("REVOKED");
  }

  @Test
  void shouldDocumentEveryStatusReturnedWhenUpdatingLineVersion() throws Exception {
    // Given
    Method update = LineApiV3.class.getMethod("updateLineVersionV3", Long.class, UpdateLineVersionModelV3.class);

    // When
    Map<String, String> responses = documentedResponses(update);

    // Then
    assertThat(responses).containsOnlyKeys("200", "400", "403", "404", "409", "412", "422", "501", "520");
  }

  @Test
  void shouldDocumentRevokedLineAsForbiddenAndMembersOutsideValidityAsConflictWhenUpdatingLineVersion() throws Exception {
    // Given
    Method update = LineApiV3.class.getMethod("updateLineVersionV3", Long.class, UpdateLineVersionModelV3.class);

    // When
    Map<String, String> responses = documentedResponses(update);

    // Then
    assertThat(responses.get("403")).contains("REVOKED");
    assertThat(responses.get("409")).contains("members outside of the new line validity");
  }

  private static Map<String, String> documentedResponses(Method method) {
    return Arrays.stream(method.getAnnotation(ApiResponses.class).value())
        .collect(Collectors.toMap(ApiResponse::responseCode, ApiResponse::description));
  }

}
