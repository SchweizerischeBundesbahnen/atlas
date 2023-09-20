package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserPermissionCreateModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldValidateUniqueApplicationType() {
    // Given
    UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
        .sbbUserId("u123456")
        .permissions(List.of(
            PermissionModel.builder()
                .permissionRestrictions(List.of())
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.TTFN)
                .build(),
            PermissionModel.builder()
                .permissionRestrictions(List.of())
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.TTFN)
                .build()
        )).
        build();
    // When
    Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
        createModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("applicationTypeUniqueInPermissions");
  }

  @Test
  void shouldValidateSboidsEmptyWhenNotWriterRole() {
    // Given
    UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
        .sbbUserId("u123456")
        .permissions(List.of(
            PermissionModel.builder()
                .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
                .role(ApplicationRole.SUPERVISOR)
                .application(ApplicationType.TTFN)
                .build()
        )).
        build();
    // When
    Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
        createModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("sboidsEmptyWhenNotWriterOrSuperUserOrBodi");
  }

  @Test
  void shouldValidateSboidsEmptyWhenApplicationTypeBodi() {
    // Given
    UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
        .sbbUserId("u123456")
        .permissions(List.of(
            PermissionModel.builder()
                .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.BODI)
                .build()
        )).
        build();
    // When
    Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
        createModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("sboidsEmptyWhenNotWriterOrSuperUserOrBodi");
  }

  @Test
  void shouldValidateSboidsEmptyWhenApplicationTypeSepodiAndSuperUser() {
    // Given
    UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
        .sbbUserId("u123456")
        .permissions(List.of(
            PermissionModel.builder()
                .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
                .role(ApplicationRole.SUPER_USER)
                .application(ApplicationType.TTFN)
                .build()
        )).
        build();
    // When
    Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
        createModel);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("sboidsEmptyWhenNotWriterOrSuperUserOrBodi");
  }

}
