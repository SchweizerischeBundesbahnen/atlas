package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.workflow.InfoPlusTerminationVotePermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.workflow.NovaTerminationVotePermissionRestrictionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PermissionModelTest {

  private static final String ALL_RESTRICTION_TYPES_ALLOWED = "allRestrictionTypesAllowed";
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldValidateSboidsEmptyWhenNotWriterRole() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.TTFN)
        .build();
    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(
        permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        ALL_RESTRICTION_TYPES_ALLOWED);
  }

  @Test
  void shouldValidateSboidsEmptyWhenApplicationTypeBodi() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.BODI)
        .build();
    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(
        permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        ALL_RESTRICTION_TYPES_ALLOWED);
  }

  @Test
  void shouldValidateSboidsEmptyWhenApplicationTypeSepodiAndSuperUser() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:test")))
        .role(ApplicationRole.SUPER_USER)
        .application(ApplicationType.TTFN)
        .build();
    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(
        permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString(
        ALL_RESTRICTION_TYPES_ALLOWED);
  }

  @Test
  void shouldAllowOnlyNovaRestrictionForReader() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new NovaTerminationVotePermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.READER)
        .application(ApplicationType.SEPODI)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldAllowOnlyInfoPlusRestrictionForReaderSepodi() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new InfoPlusTerminationVotePermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.READER)
        .application(ApplicationType.SEPODI)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowBothRestrictionForReader() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new InfoPlusTerminationVotePermissionRestrictionModel(true),
            new NovaTerminationVotePermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.READER)
        .application(ApplicationType.SEPODI)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAllowBothRestrictionForWriter() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new InfoPlusTerminationVotePermissionRestrictionModel(true),
            new NovaTerminationVotePermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.SEPODI)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAllowRestrictionForOtherApplicationExceptSepodi() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new InfoPlusTerminationVotePermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.READER)
        .application(ApplicationType.TTFN)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).hasSize(2);
  }

  @Test
  void shouldNotAllowRestrictionBulkImportForReader() {
    // Given
    PermissionModel permission = PermissionModel.builder()
        .permissionRestrictions(List.of(
            new BulkImportPermissionRestrictionModel(true)
        ))
        .role(ApplicationRole.READER)
        .application(ApplicationType.SEPODI)
        .build();

    // When
    Set<ConstraintViolation<PermissionModel>> constraintViolations = validator.validate(permission);

    // Then
    assertThat(constraintViolations).hasSize(1);
  }
}
