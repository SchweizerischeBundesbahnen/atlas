package ch.sbb.atlas.api.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.configuration.Role;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

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

  @Test
  void shouldForbidUnauthorizedClientsOnWriteEndpoints() throws Exception {
    // Given
    Method create = LineApiV3.class.getMethod("createLineVersionV3", CreateLineVersionModelV3.class);
    Method update = LineApiV3.class.getMethod("updateLineVersionV3", Long.class, UpdateLineVersionModelV3.class);

    // When / Then
    assertThat(create.getAnnotation(AuthorizedOnly.class)).isNotNull();
    assertThat(update.getAnnotation(AuthorizedOnly.class)).isNotNull();
  }

  @Test
  void shouldDenyUnauthorizedAuthorityThroughAuthorizedOnlyOnWriteEndpoints() throws Exception {
    // Given
    Method create = LineApiV3.class.getMethod("createLineVersionV3", CreateLineVersionModelV3.class);
    Method update = LineApiV3.class.getMethod("updateLineVersionV3", Long.class, UpdateLineVersionModelV3.class);
    String expectedExpression = "!hasAuthority('" + Role.AUTHORITY_UNAUTHORIZED + "')";

    // When / Then
    assertThat(preAuthorizeExpression(create)).isEqualTo(expectedExpression);
    assertThat(preAuthorizeExpression(update)).isEqualTo(expectedExpression);
  }

  @Test
  void shouldAllowUnauthorizedClientsWithoutLimitationsOnReadEndpoints() throws Exception {
    // Given
    Method paged = LineApiV3.class.getMethod("getLineVersions", Pageable.class, LineVersionV3RequestParams.class);
    Method bySlnid = LineApiV3.class.getMethod("getLineVersionsV3", String.class);

    // When / Then
    assertThat(paged.getAnnotation(UnauthorizedAllowed.class).limitations()).isEqualTo(FurtherLimitations.NONE);
    assertThat(bySlnid.getAnnotation(UnauthorizedAllowed.class).limitations()).isEqualTo(FurtherLimitations.NONE);
    assertThat(paged.getAnnotation(AuthorizedOnly.class)).isNull();
    assertThat(bySlnid.getAnnotation(AuthorizedOnly.class)).isNull();
  }

  @Test
  void shouldDeclareExactlyOneAccessAnnotationOnEveryEndpoint() {
    // Given
    Method[] endpoints = LineApiV3.class.getDeclaredMethods();

    // When / Then
    assertThat(endpoints).isNotEmpty().allSatisfy(endpoint -> {
      boolean authorizedOnly = endpoint.getAnnotation(AuthorizedOnly.class) != null;
      boolean unauthorizedAllowed = endpoint.getAnnotation(UnauthorizedAllowed.class) != null;
      assertThat(authorizedOnly ^ unauthorizedAllowed)
          .withFailMessage("%s must declare exactly one of @AuthorizedOnly / @UnauthorizedAllowed", endpoint.getName())
          .isTrue();
    });
  }

  private static String preAuthorizeExpression(Method method) {
    PreAuthorize preAuthorize = AnnotatedElementUtils.findMergedAnnotation(method, PreAuthorize.class);
    assertThat(preAuthorize).isNotNull();
    return preAuthorize.value();
  }

}
